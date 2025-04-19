# COMP4521 - QUST

The academia To-do app for HKUST students.

## Development

The project was made with `Bottom Navigation View Activity` template.

### Task Assignemnt

- Haris: backend scripts
- Marco: backend server
- Sunny
- Haoming

### File Structure

Pre-code suggested file structure for the app. This is a suggestion and can be modified as per your needs.

```text
app/
└── src/
    └── main/
        ├── java/edu.hkust.qust/              // Root package (use your app’s package name)
        │   ├── data/                         // Data layer
        │   │   ├── model/                    // Data classes/entities (e.g., Quest.kt, User.kt)
        │   │   ├── repository/               // Repositories abstracting Firebase calls and local/cache data
        │   │   └── network/                  // Firebase services and network-related code if needed
        │   ├── ui/                           // Presentation layer (all UI components)
        │   │   ├── components/               // Common reusable composables (e.g., buttons, cards)
        │   │   ├── screens/                  // Screen-specific Compose functions
        │   │   │   ├── dashboard/            // Dashboard screen composables
        │   │   │   ├── quest/                // Quest Log, New Quest, Quest Detail screens
        │   │   │   └── profile/              // Profile, settings, and academic details screens
        │   │   └── navigation/               // Navigation graph for Compose if using the Navigation component
        │   ├── viewmodel/                    // ViewModel classes handling UI logic
        │   │   ├── DashboardViewModel.kt
        │   │   ├── QuestViewModel.kt
        │   │   └── ProfileViewModel.kt
        │   ├── utils/                        // Utility classes, extensions, constants, helpers etc.
        │   └── MainActivity.kt               // Entry point; sets up the Compose UI & Navigation
        └── res/  
            ├── values/                       // Colors, dimensions, themes, etc.
            └── ...                           // Other resource directories
```

#### Data

- Model: Contains Kotlin data classes representing your business objects (like Quest, User, etc.). These objects should be designed to directly mirror your Firestore documents.
- Repository: Acts as the access point for data sources. It will encapsulate the logic required to fetch, cache, and update your data from Firebase. For instance, you could have a QuestRepository that interacts with Firestore and exposes suspend functions or flows
- Network: If you’re using Firebase Cloud Messaging or other services, you can encapsulate those integration methods here.

#### UI

- Components: Create reusable UI components that can be shared across multiple screens. This helps keep your design consistent.
- Screens: Organize each major view (Dashboard, Quest Log, Profile, etc.) into its own folder with dedicated Compose files. This separation makes it easy to maintain and enhance individual screens.
- Navigation: Use Compose Navigation to manage screen transitions neatly.

#### ViewModel

- Each major screen or function should have its own ViewModel. The ViewModel will handle state changes, transform repository data for the UI, and expose state as Compose-observable streams (like StateFlow or LiveData).
- This ensures your UI remains reactive and your business logic encapsulated.

#### Utils

Place helper methods, extension functions, and constants here. This keeps your code DRY and organized.

