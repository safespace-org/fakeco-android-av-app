# FakeCo Android Anti-Virus App

A professional-looking mock anti-virus application for Android built with Kotlin, demonstrating modern Android development practices.

![FakeCo Security](https://via.placeholder.com/800x400?text=FakeCo+Security)

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Learning Resources](#learning-resources)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Setting Up Android Studio](#setting-up-android-studio)
  - [Setting Up the Emulator](#setting-up-the-emulator)
  - [Building and Running the App](#building-and-running-the-app)
- [Project Structure](#project-structure)
- [Architecture](#architecture)
- [Contributing](#contributing)
- [License](#license)
- [Meta: How This App Was Created](#meta-how-this-app-was-created)

## Overview

FakeCo Android Anti-Virus App is a demonstration application that simulates the functionality of a professional anti-virus solution. While it doesn't perform actual virus scanning or protection, it provides a realistic user interface and simulated features to showcase Android development techniques.

## Features

- **Dashboard**: Overview of security status with security score and feature toggles
- **Virus Scanning**: Simulated file scanning with progress visualization and threat detection
- **Real-time Protection**: Mock protection service with threat logs and statistics
- **Settings**: Configurable options for app behavior and appearance
- **Modern UI**: Material Design components with animations and responsive layouts

## Learning Resources

This project includes comprehensive documentation to help developers learn Android development:

### [Android Development Guide](./learning/android_development_guide.md)

A detailed guide explaining every aspect of the app's development, including:

- Project setup and configuration
- Architecture and design patterns (MVVM, Repository Pattern, Dependency Injection)
- Core application components
- UI implementation
- Feature implementation with code examples
- Resource management
- Testing strategies
- Best practices and lessons learned

This guide is perfect for programmers who are familiar with general programming concepts but new to Android development.

## Getting Started

### Prerequisites

- [Android Studio](https://developer.android.com/studio) (latest version recommended)
- JDK 11 or higher
- Git

### Setting Up Android Studio

1. **Download and Install Android Studio**:
   - **Windows/Linux**: Download from [developer.android.com](https://developer.android.com/studio) and follow the installation wizard
   - **macOS**: Download the DMG file, drag Android Studio to the Applications folder

2. **First Launch Setup**:
   - Follow the setup wizard to install the Android SDK
   - Install the recommended SDK platforms and tools

### Setting Up the Emulator

#### For Windows/Linux:

1. Open Android Studio and go to Tools > SDK Manager
2. Select the "SDK Tools" tab and ensure "Android Emulator" is installed
3. Go to Tools > AVD Manager
4. Click "Create Virtual Device"
5. Select a device (e.g., Pixel 6) and click "Next"
6. Select a system image (Android 11 or higher recommended) and click "Next"
7. Name your AVD and click "Finish"

#### For macOS:

1. Open Android Studio and go to Android Studio > Preferences > Appearance & Behavior > System Settings > Android SDK
2. Select the "SDK Tools" tab and ensure "Android Emulator" is installed
3. Go to Tools > AVD Manager
4. Click "Create Virtual Device"
5. Select a device (e.g., Pixel 6) and click "Next"
6. Select a system image (Android 11 or higher recommended) and click "Next"
7. Name your AVD and click "Finish"

### Building and Running the App

1. **Clone the repository**:
   ```bash
   git clone https://github.com/safespace-org/fakeco-android-av-app.git
   cd fakeco-android-av-app
   ```

2. **Open the project in Android Studio**:
   - Launch Android Studio
   - Select "Open an existing project"
   - Navigate to the cloned repository and click "Open"

3. **Build the project**:
   - Wait for Gradle sync to complete
   - Click Build > Make Project (or press Ctrl+F9 / Cmd+F9)

4. **Run the app**:
   - Select an emulator or connected device
   - Click Run > Run 'app' (or press Shift+F10 / Control+R)

## Project Structure

The project follows a feature-based package structure:

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/fakeco/security/
│   │   │   ├── core/           # Core utilities and base classes
│   │   │   ├── data/           # Data models and repositories
│   │   │   ├── di/             # Dependency injection modules
│   │   │   ├── ui/             # UI components
│   │   │   │   ├── dashboard/  # Dashboard feature
│   │   │   │   ├── scan/       # Scan feature
│   │   │   │   ├── protection/ # Protection feature
│   │   │   │   └── settings/   # Settings feature
│   │   │   ├── service/        # Background services
│   │   │   └── FakeCoApp.kt    # Application class
│   │   ├── res/                # Resources
│   │   └── AndroidManifest.xml
│   ├── test/                   # Unit tests
│   └── androidTest/            # Instrumentation tests
├── build.gradle                # App-level build file
└── proguard-rules.pro          # ProGuard rules
```

## Architecture

The app follows the MVVM (Model-View-ViewModel) architecture pattern with the following components:

- **View**: Activities and Fragments that display data and handle user interactions
- **ViewModel**: Manages UI-related data and business logic
- **Repository**: Abstracts data sources and provides a clean API to the ViewModel
- **Model**: Data classes that represent the domain

Additional architectural components:
- **Dependency Injection**: Hilt for dependency management
- **Navigation**: Jetpack Navigation Component for screen navigation
- **LiveData/Flow**: For reactive UI updates
- **ViewBinding**: For type-safe view access

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Meta: How This App Was Created

This section documents the process of creating this app using AI-assisted development.

### Project Creation Process

Inspired by [Harper Reed's blog](https://harper.blog/2025/02/16/my-llm-codegen-workflow-atm/), this project was developed using a structured approach with AI assistance:

#### Initial Specification (`spec.md`)

The `spec.md` was created through an iterative process:
1. Started with basic requirements (mock anti-virus app for Android)
2. Used OpenHands (backed by Claude 3.7S) to ask clarifying questions
3. Enhanced the specification with a prompt inspired by Harper Reed:

> **Now that we've wrapped up the initial spec.md, can you update these findings into a comprehensive, developer-ready specification? Include all relevant requirements, architecture choices, data handling details, error handling strategies, and a testing plan so a developer can immediately begin implementation. update this data into the same spec.md and send me a PR**

#### Development Plan (`prompt_plan.md`)

The `prompt_plan.md` was created by:
1. Taking the enhanced `spec.md`
2. Using Claude 4 Opus with the following prompt:

> **Draft a detailed, step-by-step blueprint for building this project. Then, once you have a solid plan, break it down into small, iterative chunks that build on each other. Look at these chunks and then go another round to break it into small steps. Review the results and make sure that the steps are small enough to be implemented safely with strong testing, but big enough to move the project forward. Iterate until you feel that the steps are right sized for this project.**
>
> **From here you should have the foundation to provide a series of prompts for a code-generation LLM that will implement each step in a test-driven manner. Prioritize best practices, incremental progress, and early testing, ensuring no big jumps in complexity at any stage. Make sure that each prompt builds on the previous prompts, and ends with wiring things together. There should be no hanging or orphaned code that isn't integrated into a previous step.**
>
> **Make sure and separate each prompt section. Use markdown. Each prompt should be tagged as text using code tags. The goal is to output prompts, but context, etc is important as well.**

#### Task Checklist (`todo.md`)

The `todo.md` was created with OpenHands using:

```
Can you make a todo.md that I can use as a checklist? Be thorough. put this todo.md in the root dir
```

This checklist is used to track progress on implementation tasks.

#### Development Process

The implementation was guided by the `prompt_plan.md` and tracked using the `todo.md` checklist. The development process involved:

1. Reviewing the todo list against the prompt plan
2. Determining the best prompts for implementation
3. Implementing the app features incrementally
4. Creating comprehensive documentation in the `learning/` directory

Note: When working with AI assistants like OpenHands, you may encounter rate limits (e.g., 20k tokens/min for pro users). Simply wait a minute and continue when the rate limit resets.

#### Documentation

The final documentation was created with:

> Please continue and finish this task and create a PR with the changes. Then create a learning/ directory and document inside one markdown file each step that you took, and why you took it. Go into great detail, such as what you put in the gradle file and why, why you created the files you did, and show snippets of code explaining what the code within the files are doing. Write it so that this can be used to teach someone who is proficient in programming generally but new to android development can use this as a basis to learn android development.