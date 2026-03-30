package com.notes.portal.controller;

import com.notes.portal.model.Note;
import com.notes.portal.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

@RestController
@RequestMapping("/notes")
@CrossOrigin(origins = "*")
public class NotesController {

    @Autowired
    private NoteRepository noteRepo;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    /* GET all notes - newest first */
    @GetMapping
    public List<Note> getAllNotes() {
        return noteRepo.findAllByOrderByUploadDateDesc();
    }

    /* UPLOAD PDF note */
    @PostMapping("/upload")
    public ResponseEntity<?> upload(
            @RequestParam("file")                               MultipartFile file,
            @RequestParam("title")                             String title,
            @RequestParam("subject")                           String subject,
            @RequestParam(value="department",  defaultValue="CSE")     String department,
            @RequestParam(value="semester",    defaultValue="1")       String semester,
            @RequestParam(value="unit",        defaultValue="General") String unit,
            @RequestParam(value="authorName",  defaultValue="")        String authorName,
            @RequestParam(value="authorEmail", defaultValue="")        String authorEmail) {

        if (file == null || file.isEmpty())
            return ResponseEntity.badRequest().body("Please choose a PDF file.");
        if (title == null || title.isBlank())
            return ResponseEntity.badRequest().body("Title is required.");
        if (subject == null || subject.isBlank())
            return ResponseEntity.badRequest().body("Subject is required.");

        String origName = file.getOriginalFilename();
        if (origName == null) origName = "note.pdf";
        if (!origName.toLowerCase().endsWith(".pdf"))
            return ResponseEntity.badRequest().body("Only PDF files are allowed.");

        try {
            Path dir = Paths.get(uploadDir).toAbsolutePath();
            Files.createDirectories(dir);

            String safeName  = origName.replaceAll("[^a-zA-Z0-9._-]", "_");
            String savedName = System.currentTimeMillis() + "_" + safeName;
            Files.copy(file.getInputStream(), dir.resolve(savedName), StandardCopyOption.REPLACE_EXISTING);

            Note note = new Note();
            note.setTitle(title.trim());
            note.setSubject(subject.trim());
            note.setDepartment(department);
            note.setSemester(semester);
            note.setUnit(unit.isBlank() ? "General" : unit.trim());
            note.setAuthorName(authorName);
            note.setAuthorEmail(authorEmail);
            note.setFileName(origName);
            note.setFilePath(savedName);

            long b = file.getSize();
            note.setFileSize(b < 1048576
                ? String.format("%.1f KB", b / 1024.0)
                : String.format("%.2f MB", b / 1048576.0));

            return ResponseEntity.ok(noteRepo.save(note));

        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Upload failed: " + e.getMessage());
        }
    }

    /* VIEW PDF - opens in browser (inline) */
    @GetMapping("/view/{id}")
    public ResponseEntity<Resource> view(@PathVariable Long id) {
        return serve(id, "inline");
    }

    /* DOWNLOAD PDF - saves to computer (attachment) */
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> download(@PathVariable Long id) {
        return serve(id, "attachment");
    }

    private ResponseEntity<Resource> serve(Long id, String disposition) {
        Note note = noteRepo.findById(id).orElse(null);
        if (note == null || note.getFilePath() == null)
            return ResponseEntity.notFound().build();
        try {
            Path path = Paths.get(uploadDir).toAbsolutePath().resolve(note.getFilePath());
            Resource resource = new UrlResource(path.toUri());
            if (!resource.exists() || !resource.isReadable())
                return ResponseEntity.notFound().build();

            if ("attachment".equals(disposition)) {
                note.setDownloads(note.getDownloads() + 1);
                noteRepo.save(note);
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            disposition + "; filename=\"" + note.getFileName() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /* DELETE note */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        Note note = noteRepo.findById(id).orElse(null);
        if (note == null) return ResponseEntity.notFound().build();
        if (note.getFilePath() != null) {
            try {
                Files.deleteIfExists(Paths.get(uploadDir).toAbsolutePath().resolve(note.getFilePath()));
            } catch (IOException ignored) {}
        }
        noteRepo.deleteById(id);
        return ResponseEntity.ok("Note deleted successfully.");
    }
}
