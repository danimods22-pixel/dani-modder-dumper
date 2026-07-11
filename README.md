# 🎮 DANI MODDER DUMPER

**Advanced Memory Dumper untuk Non-Unity Games dengan Frida Integration**

![Version](https://img.shields.io/badge/version-1.0-green)
![API](https://img.shields.io/badge/minAPI-16-blue)
![License](https://img.shields.io/badge/license-MIT-black)

---

## 📋 Fitur Utama

✅ **Frida Attachment** - Attach debugger ke proses game  
✅ **Memory Dumping** - Dump library `libgame.so` ke file binary  
✅ **Base Address Finder** - Cari base address library otomatis  
✅ **Memory Search** - Search value (int, float, string) dalam memory  
✅ **Log Export** - Export hasil dengan base address ke .txt  
✅ **Dark UI** - Interface tema gelap dengan tombol glow  

---

## 🚀 Quick Start

### Prerequisites
- Android 4.1+ (API 16+)
- Frida server terinstall di device
- Root access (untuk dump memory)
- libgame.so di target game

### Installation

1. **Clone repository:**
   ```bash
   git clone https://github.com/danimods22-pixel/dani-modder-dumper.git
   cd dani-modder-dumper
   ```

2. **Build dengan AIDE atau gradle:**
   ```bash
   ./gradlew build
   ```

3. **Install APK:**
   ```bash
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

---

## 📱 Cara Penggunaan

### 1️⃣ **Memilih Target Game**
- Aplikasi menampilkan list game terinstall
- Tap game yang ingin di-mod
- Process name akan otomatis terisi

### 2️⃣ **Attach Frida**
1. Pastikan Frida server sudah jalan di device:
   ```bash
   # Di device (root shell)
   frida-server &
   ```

2. Tap tombol **ATTACH**
3. Aplikasi akan menampilkan PID target
4. Jalankan di PC:
   ```bash
   frida -H localhost:27042 -p <PID>
   ```

### 3️⃣ **Dump libgame.so**
1. Tap tombol **DUMP**
2. Aplikasi akan:
   - Cari base address libgame.so
   - Dump ke `/sdcard/DaniModder/dump.bin`
   - Tampilkan base address & size

**Output contoh:**
```
✓ Library Dumping Started
Library: libgame.so
PID: 12345
Base Address: 0x7f4a2b1000
Library Size: 25.50 MB
✓ Successfully dumped to: /sdcard/DaniModder/dump.bin
```

### 4️⃣ **Search Memory Value**
1. Tap tombol **SEARCH**
2. Pilih tipe: `int`, `float`, atau `string`
3. Masukkan nilai yang dicari
4. Aplikasi menampilkan semua offset ditemukan

**Contoh search:**
```
Type: int | Value: 9999
Found 45 matches:
  [0x00001234] [0x00005678] [0x00009ABC]
  [0x0000DEF0] [0x00013579] ...
```

### 5️⃣ **Export Log**
1. Tap tombol **EXPORT**
2. Log otomatis disimpan ke:
   ```
   /sdcard/DaniModder/DaniModder_2024-01-15_143022.txt
   ```

---

## 🔧 Tutorial Frida Setup

### Install Frida di PC
```bash
pip install frida-tools
pip install frida
```

### Download Frida Server
```bash
# Download versi sesuai device (arm, arm64, x86, dll)
# https://github.com/frida/frida/releases

# Contoh untuk arm64:
wget https://github.com/frida/frida/releases/download/16.0.0/frida-server-16.0.0-android-arm64.xz
xz -d frida-server-16.0.0-android-arm64.xz
```

### Push ke Device
```bash
adb push frida-server-16.0.0-android-arm64 /data/local/tmp/
adb shell chmod +x /data/local/tmp/frida-server-16.0.0-android-arm64
```

### Start Frida Server
```bash
# Via adb
adb shell /data/local/tmp/frida-server-16.0.0-android-arm64 &

# Atau via SSH jika device punya sshd
ssh root@<device-ip> /data/local/tmp/frida-server-16.0.0-android-arm64 &
```

### Test Frida Connection
```bash
# Dari PC, forward port
adb forward tcp:27042 tcp:27042

# List proses
frida-ps -H localhost:27042

# Attach ke game (ganti PID)
frida -H localhost:27042 -p <PID>
```

---

## 📦 Extract libgame.so dari APK

### Cara 1: Menggunakan 7-Zip
```bash
# APK adalah zip file
7z x game.apk lib/arm64-v8a/libgame.so

# Output akan di folder lib/arm64-v8a/
```

### Cara 2: Menggunakan Python
```python
import zipfile

with zipfile.ZipFile('game.apk', 'r') as zip_ref:
    zip_ref.extract('lib/arm64-v8a/libgame.so', 'extracted/')
```

### Cara 3: Dump dari Device
```bash
# List lib di device
adb shell ls -la /data/app/com.game.name-1/lib/arm64/

# Pull ke PC
adb pull /data/app/com.game.name-1/lib/arm64/libgame.so .
```

---

## 🔎 Frida Script Examples

### Dump String Memory
```javascript
// Dalam frida-repl

const libc = Module.findBaseAddress('libc.so');
const target = libc.add(0x12345);
const str = target.readCString();
console.log("String: " + str);
```

### Hook Function
```javascript
const target = Module.findBaseAddress('libgame.so');
const funcAddr = target.add(0x5678);

Interceptor.attach(funcAddr, {
    onEnter: function(args) {
        console.log("Called with args: " + args[0]);
    },
    onLeave: function(retval) {
        console.log("Return: " + retval);
    }
});
```

### Find String in Memory
```javascript
const results = Memory.scanSync('libgame.so', 'MySearchString');
console.log("Found at: " + results);
```

---

## 📂 Project Structure

```
dani-modder-dumper/
├── app/
│   ├── src/main/
│   │   ├── java/com/dani/modder/
│   │   │   ├── MainActivity.java
│   │   │   ├── FridaAttacher.java
│   │   │   ├── LibDumper.java
│   │   │   ├── MemorySearcher.java
│   │   │   └── ExportLogger.java
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   │   ├── activity_main.xml
│   │   │   │   └── search_dialog.xml
│   │   │   ├── values/
│   │   │   │   ├── colors.xml
│   │   │   │   ├── strings.xml
│   │   │   │   └── styles.xml
│   │   │   ├── drawable/
│   │   │   │   └── button_glow.xml
│   │   │   └── AndroidManifest.xml
│   │   └── build.gradle
│   └── proguard-rules.pro
├── README.md (ini)
└── .gitignore
```

---

## ⚠️ Requirements & Permissions

### Android Permissions
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.QUERY_ALL_PACKAGES" />
```

### System Requirements
- Device dengan /proc filesystem (Linux-based)
- `pidof` command tersedia (biasanya ada di rom custom)
- Root access untuk read /proc/[pid]/mem

---

## 🐛 Troubleshooting

### Error: "Process not found"
- Pastikan game sudah di-launch
- Gunakan nama package, bukan display name

### Error: "libgame.so not found in maps"
- Beberapa game tidak punya libgame.so
- Check nama library yang sebenarnya dengan:
  ```bash
  adb shell "cat /proc/$(pidof com.game.name)/maps | grep \.so"
  ```

### Frida Connection Timeout
- Pastikan adb forward sudah aktif
- Check firewall settings
- Restart frida-server

### Dump File Error
- Pastikan /sdcard writable dan ada space cukup
- Grant storage permissions di Android settings

---

## 📝 License

MIT License - Bebas digunakan untuk keperluan research & modding.

---

## 🤝 Contributing

Contribusi welcome! Silakan buat issue atau pull request untuk improvement.

---

## 📞 Support & Contact

- 👤 Creator: **danimods22-pixel**
- 📧 Issues: Report via GitHub Issues
- 💬 Discussions: GitHub Discussions

---

**Made with ❤️ for Game Modders**
