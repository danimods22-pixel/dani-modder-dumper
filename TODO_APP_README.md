# 📝 My To-Do List Application

**Simple yet powerful to-do list app with local storage functionality**

![Version](https://img.shields.io/badge/version-1.0-blue)
![API](https://img.shields.io/badge/minAPI-16-green)
![License](https://img.shields.io/badge/license-MIT-black)

---

## ✨ Features

✅ **Add To-Do Items** - Quickly add new tasks  
✅ **Local Storage** - Save tasks using SharedPreferences  
✅ **Delete Tasks** - Long press to remove items  
✅ **Persistent Storage** - Tasks saved even after app restart  
✅ **Clean UI** - Modern Material Design interface  
✅ **No Internet Required** - Fully offline functionality  

---

## 🎯 Quick Start

### Prerequisites
- Android 4.1+ (API 16+)
- AIDE IDE or Android Studio

### Installation

1. **Navigate to project:**
   ```bash
   cd dani-modder-dumper/todo-app
   ```

2. **Build with AIDE:**
   - Open project in AIDE
   - Tools → Build Project
   - Install APK on device

3. **Or use Android Studio:**
   ```bash
   ./gradlew build
   ./gradlew installDebug
   ```

---

## 📱 Usage

### Adding a To-Do
1. Type your task in the input field
2. Tap **"Add"** button
3. Task will be saved automatically

### Deleting a To-Do
1. **Long press** on a task
2. Confirm deletion
3. Task removed from list

### Storage
- All tasks are saved in **SharedPreferences**
- Data persists across app restarts
- No internet connection needed

---

## 🗂️ Project Structure

```
todo-app/
├── src/main/
│   ├── java/com/dani/todo/
│   │   ├── MainActivity.java          # Main UI activity
│   │   └── TodoStorage.java           # Local storage handler
│   ├── res/
│   │   ├── layout/
│   │   │   └── activity_main.xml      # UI layout
│   │   ├── drawable/
│   │   │   └── button_add.xml         # Button styling
│   │   └── values/
│   │       ├── colors.xml
│   │       ├── strings.xml
│   │       └── styles.xml
│   └── AndroidManifest.xml
├── build.gradle
├── proguard-rules.pro
└── README.md
```

---

## 💾 Local Storage Implementation

### SharedPreferences
The app uses Android's **SharedPreferences** for local storage:

```java
// Save todos
storage.saveTodos(todos);

// Load todos
ArrayList<String> todos = storage.loadTodos();
```

### Data Format
- Todos are stored as a single string
- Items separated by `||||` delimiter
- Automatically handles serialization/deserialization

### Code Example

**Saving:**
```java
private void saveTodos(ArrayList<String> todos) {
    StringBuilder sb = new StringBuilder();
    for (String todo : todos) {
        sb.append(todo).append("||||");
    }
    editor.putString(KEY_TODOS, sb.toString());
    editor.apply();
}
```

**Loading:**
```java
public ArrayList<String> loadTodos() {
    String data = prefs.getString(KEY_TODOS, "");
    String[] items = data.split("\\|\\|\\|\\|");
    // Convert to ArrayList
}
```

---

## 🎨 UI Components

### Layout Structure
- **Header** - App title with blue color
- **Input Section** - EditText + Add button
- **Task List** - ListView displaying all todos
- **Footer** - Helper text

### Material Design
- Clean modern interface
- Responsive buttons
- Clear visual hierarchy
- Proper spacing and padding

---

## 📱 Requirements & Permissions

### Permissions
```xml
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```

### Dependencies
```gradle
implementation 'androidx.appcompat:appcompat:1.0.0'
implementation 'com.google.android.material:material:1.0.0'
```

---

## 🐛 Troubleshooting

### Tasks not saving
- Check if write permissions are granted
- Ensure app has storage permissions
- Check logcat for errors

### Tasks disappearing
- Check SharedPreferences settings
- Make sure app isn't clearing data on exit
- Check storage space on device

### UI not updating
- Call `adapter.notifyDataSetChanged()` after changes
- Ensure UI updates happen on main thread

---

## 🚀 Future Enhancements

- [ ] Task categories/tags
- [ ] Due dates and reminders
- [ ] Priority levels
- [ ] Search functionality
- [ ] Cloud sync (Firebase)
- [ ] Dark mode
- [ ] Export/Import CSV
- [ ] Task completion tracking

---

## 📝 License

MIT License - Free to use and modify

---

## 🤝 Contributing

Contributions welcome! Feel free to:
- Report bugs
- Suggest features
- Submit pull requests

---

## 👤 Author

**danimods22-pixel**  
📧 Email: danimods22@gmail.com  
🔗 GitHub: https://github.com/danimods22-pixel

---

**Made with ❤️ for productivity**
