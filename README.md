# Code Snippet Manager

A complete desktop application for managing code snippets built with **Java Swing**, **MongoDB**, and modern UI components.

## Features

### 🔐 Authentication System
- User registration with email validation
- Secure password hashing using BCrypt
- Login/Logout functionality
- Session management

### 📂 Snippet Management
- Add, edit, delete, and view code snippets
- Syntax highlighting for 30+ programming languages
- Tags support for better organization
- Description field for additional context
- Timestamp tracking (created/updated dates)

### 🔍 Search & Filter
- Full-text search across title, language, tags, and description
- Filter by programming language
- Filter by tags
- Real-time search as you type

### ☁️ Database
- MongoDB for persistent storage
- Separate collections for users and snippets
- User-based data isolation
- Automatic indexing for performance

### 📤 PDF Export
- Export individual snippets to PDF
- Export all snippets to single PDF
- Professional formatting with syntax-styled code

### 🎨 UI/UX
- Modern dark theme (FlatLaf)
- Syntax highlighting with RSyntaxTextArea
- Hover effects and smooth transitions
- Confirmation dialogs for destructive actions
- Responsive and intuitive interface

## Project Structure

```
src/main/java/com/snippetmanager/
├── Main.java                    # Application entry point
├── config/
│   └── DatabaseConfig.java      # MongoDB connection configuration
├── model/
│   ├── User.java                # User entity
│   └── Snippet.java             # Snippet entity
├── dao/
│   ├── UserDAO.java             # User data access layer
│   └── SnippetDAO.java          # Snippet data access layer
├── service/
│   ├── UserService.java         # User business logic
│   └── SnippetService.java      # Snippet business logic
├── ui/
│   ├── LoginUI.java             # Login screen
│   ├── SignupUI.java            # Registration screen
│   ├── DashboardUI.java         # Main application window
│   ├── SnippetFormDialog.java   # Add/Edit snippet form
│   └── ViewSnippetDialog.java   # View snippet details
└── utils/
    ├── PasswordHasher.java      # BCrypt password hashing
    ├── PDFExporter.java         # PDF export functionality
    ├── DateUtil.java            # Date formatting utilities
    └── ThemeManager.java        # UI theme management
```

## Prerequisites

1. **Java 17** or higher
2. **MongoDB** (local or remote instance)
3. **Maven** (for building)

## Installation & Setup

### 1. Install MongoDB

**Windows:**
```powershell
# Download and install MongoDB Community Server from:
# https://www.mongodb.com/try/download/community

# Start MongoDB service
net start MongoDB
```

**macOS:**
```bash
brew tap mongodb/brew
brew install mongodb-community
brew services start mongodb-community
```

**Linux (Ubuntu):**
```bash
sudo apt-get install mongodb
sudo systemctl start mongodb
```

### 2. Build the Project

```bash
cd SnippetManager
mvn clean package
```

### 3. Run the Application

```bash
java -jar target/code-snippet-manager-1.0-SNAPSHOT.jar
```

Or directly with Maven:

```bash
mvn exec:java -Dexec.mainClass="com.snippetmanager.Main"
```

## Dependencies

| Dependency | Version | Purpose |
|------------|---------|---------|
| MongoDB Java Driver | 4.11.1 | Database connectivity |
| Apache PDFBox | 3.0.1 | PDF export functionality |
| RSyntaxTextArea | 3.3.4 | Syntax highlighting |
| FlatLaf | 3.2.5 | Modern UI Look and Feel |
| jBCrypt | 0.4 | Password hashing |
| Lombok | 1.18.30 | Boilerplate reduction |

## Database Schema

### Users Collection
```javascript
{
  _id: ObjectId,
  username: String (unique),
  email: String (unique),
  passwordHash: String,
  createdAt: DateTime,
  updatedAt: DateTime
}
```

### Snippets Collection
```javascript
{
  _id: ObjectId,
  userId: ObjectId (reference),
  title: String,
  programmingLanguage: String,
  code: String,
  tags: [String],
  description: String,
  createdAt: DateTime,
  updatedAt: DateTime
}
```

## Usage Guide

### First Run
1. Start the application
2. Click "Sign Up" to create a new account
3. Log in with your credentials
4. Start adding code snippets!

### Managing Snippets
- **Add**: Click "+ New Snippet" button or use File > New
- **Edit**: Select a snippet and click "Edit"
- **Delete**: Select a snippet and click "Delete" (confirmation required)
- **View**: Double-click a snippet or select and click "View"

### Search & Filter
- Type in the search box for real-time results
- Use the filter dropdown to search by specific fields
- Use language filter to show only specific programming languages

### Export
- Single snippet: Select and click "Export PDF"
- All snippets: File > Export All to PDF

## Keyboard Shortcuts

| Shortcut | Action |
|----------|--------|
| Ctrl+N | Add new snippet |
| F5 | Refresh snippet list |
| Double-click | View snippet |

## Troubleshooting

### MongoDB Connection Issues
- Verify MongoDB is running: `mongosh --eval "db.adminCommand({ping:1})"`
- Check MongoDB port (default: 27017)
- Ensure firewall allows MongoDB connections

### Application Won't Start
- Check Java version: `java -version` (must be 17+)
- Verify all dependencies are downloaded: `mvn clean install`

### PDF Export Fails
- Ensure you have write permissions to the selected directory
- Check available disk space

## Architecture Overview

The application follows a **layered architecture** with clear separation of concerns:

1. **Presentation Layer (UI)**: Java Swing forms and dialogs
2. **Service Layer**: Business logic and validation
3. **Data Access Layer (DAO)**: MongoDB operations
4. **Model Layer**: Entity classes (User, Snippet)
5. **Utils**: Cross-cutting concerns (security, export, themes)

## Security Features

- BCrypt password hashing with salt
- Input validation and sanitization
- Duplicate username/email prevention
- User data isolation (users can only access their own snippets)

## Future Enhancements

- [ ] Cloud sync functionality
- [ ] Code snippet sharing
- [ ] Import from GitHub Gist
- [ ] Plugin system for custom languages
- [ ] Dark/Light theme toggle
- [ ] Keyboard shortcuts customization

## License

This project is created for academic purposes. Feel free to use and modify as needed.

## Author

Created for academic submission and viva demonstration.

---

**Happy Coding! 🚀**
