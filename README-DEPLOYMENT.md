# Deployment Guide - Render

This guide explains how to deploy the College Notes backend on Render with PostgreSQL.

## Prerequisites

1. A Render account (https://render.com)
2. A PostgreSQL database on Render
3. An OpenAI API key
4. Your GitHub repository connected to Render

## Deployment Steps

### 1. Connect Repository to Render

1. Go to https://render.com/dashboard
2. Click "New +" → "Web Service"
3. Select "Build and deploy from a Git repository"
4. Connect your GitHub repository
5. Select the branch to deploy (usually `main` or `master`)

### 2. Configure the Web Service

**Runtime:** Java (17)  
**Build Command:** `mvn clean package -DskipTests`  
**Start Command:** `java -jar target/portal-1.0.0.jar`

### 3. Create PostgreSQL Database

1. In Render Dashboard, click "New +" → "PostgreSQL"
2. Enter database name: `collegenotes_db`
3. Region: Choose the same region as your web service
4. Plan: Free tier or your preferred plan
5. Click "Create Database"

### 4. Set Environment Variables

In your Render web service settings, add the following environment variables:

| Key | Value |
|-----|-------|
| `PORT` | `8080` |
| `DATABASE_URL` | Copy from PostgreSQL service (Internal Database URL) |
| `DB_USER` | PostgreSQL user (usually `postgres`) |
| `DB_PASSWORD` | PostgreSQL password |
| `OPENAI_API_KEY` | Your OpenAI API key |

**Note:** You can also link the PostgreSQL database directly using Render's UI to auto-populate `DATABASE_URL`.

### 5. Deploy

1. Commit and push your changes to GitHub:
   ```bash
   git add .
   git commit -m "Configure Render deployment"
   git push
   ```

2. Render will automatically detect the push and start building
3. Monitor the deployment in the Render dashboard
4. Once deployed, you'll get a live URL

## Environment Variables Details

### Automatic Configuration

- **DATABASE_URL**: Format for PostgreSQL: `postgresql://user:password@host:port/database`
- **PORT**: Render automatically sets this based on the service plan

### Manual Configuration

- **OPENAI_API_KEY**: Get from https://platform.openai.com/api-keys
- **DB_USER**: PostgreSQL username (if not using Render's internal URL)
- **DB_PASSWORD**: PostgreSQL password (if not using Render's internal URL)

## Database Setup

The application uses Hibernate with `ddl-auto=update`, which will:
- Automatically create tables on first run
- Update schema if entities change
- NOT drop tables on restart

### Accessing the Database

To check tables or run queries:
1. Go to your PostgreSQL service in Render
2. Click "Connect" → "External Database URL"
3. Use a PostgreSQL client like pgAdmin or DBeaver

## Troubleshooting

### 502 Bad Gateway Errors
- Check if the application is running: `java -jar target/portal-1.0.0.jar`
- Check logs in Render dashboard for errors
- Verify `PORT` environment variable is set

### Database Connection Errors
- Verify `DATABASE_URL` is correct
- Check if the PostgreSQL service is active
- Ensure firewall/network rules allow connection

### Build Failures
- Check Maven build logs
- Ensure Java 17 is being used: `<java.version>17</java.version>` in pom.xml
- Verify all dependencies are available

## Useful Commands

```bash
# Build locally before pushing
mvn clean package

# Test the application locally
java -jar target/portal-1.0.0.jar

# View Render logs (requires CLI)
render logs collegenotes-backend
```

## File Upload Handling

The application allows file uploads to the `uploads/` directory. When deployed on Render:

- Uploads stored in the ephemeral filesystem will be lost on redeploys
- For persistent storage, consider using:
  - AWS S3
  - Azure Blob Storage
  - Render's persistent disk

## Next Steps

- Monitor application performance in Render dashboard
- Set up error tracking (e.g., Sentry)
- Configure custom domain for your service
- Set up automated backups for PostgreSQL

For more help, visit: https://render.com/docs
