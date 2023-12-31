# Chika_Tunes Music Player App

Chika_Tunes is an Android music player application that provides a seamless and feature-rich experience for music enthusiasts. This application supports both .mp3 and .wav file formats, offering users the flexibility to manage and enjoy their music collection.

![Design sans titre (1)](https://github.com/ikram28/Chika-Tunes/assets/86806466/5faf537e-c645-4a4f-8770-41d57bdd68fa)


## Features
### 1. File Format Support
The Chika Tunes app supports both .mp3 and .wav file formats, ensuring compatibility with a diverse range of music files.

### 2. Runtime Permission Handling
To access external storage and retrieve music files, the application includes a robust runtime permission handling mechanism. Users are prompted to grant necessary permissions, enhancing security and user control.

### 3. User Interface
The app boasts an intuitive and visually appealing user interface, contributing to a seamless user experience. The ListView layout efficiently displays the list of available songs.

### 4. Playlist Management
Chika Tunes allows users to manage playlists efficiently. The app organizes songs in a list, and users can easily navigate through their collection.

### 5. Custom Adapter
The custom adapter implemented in the app enhances the display of songs in the ListView. It extracts song title and artist information from file names and displays them appropriately.

### 6. Media Metadata Retrieval
Chika Tunes uses MediaMetadataRetriever to extract metadata, including embedded album art, enhancing the visual appeal of the user interface.

### 7. Playback Controls
The app provides standard playback controls such as play, pause, next, previous, fast forward, and rewind, ensuring a comprehensive music playback experience.

### 8. Lyrics Integration
For supported songs, Chika Tunes integrates lyrics, enriching the user experience with additional information about the currently playing song.

## Technical Details
### 1. Permission Handling
The application requests and handles the READ_EXTERNAL_STORAGE permission gracefully, ensuring a secure and user-friendly experience.

### 2. Threading
The use of threads in the app for updating the seek bar and handling time updates during playback demonstrates effective concurrency management.

### 3. MediaPlayer
Chika Tunes employs the Android MediaPlayer class for audio playback, offering a reliable and feature-rich solution for playing music.

## Future Development
The application has a solid foundation, and future updates may include additional features, user interface enhancements, and support for more file formats. 

## Authors:
- Ikram Belmadani
- Chaimae Biyaye
