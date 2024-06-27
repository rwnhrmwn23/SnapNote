# SnapNote - Android Architecture Samples

These samples showcase different architectural approaches to developing Android apps. 

## Screenshots

![SnapNote](https://github.com/rwnhrmwn23/SnapNote/assets/25237512/59c26686-66ca-4fa6-b142-d78ef8e8ec60)

## About

The app in this project aims to be simple enough that you can understand it quickly, but complex enough to showcase difficult design decisions and testing scenarios.

## Features

- User Interface built with **[Jetpack Compose](https://developer.android.com/jetpack/compose)** 
- A single-activity architecture, using **[Navigation Compose](https://developer.android.com/jetpack/compose/navigation)**.
- A presentation layer that contains a Compose screen (View) and a **ViewModel** per screen (or feature).
- Reactive UIs using **[Flow](https://developer.android.com/kotlin/flow)** and **[coroutines](https://kotlinlang.org/docs/coroutines-overview.html)** for asynchronous operations.
- A **data layer** with a repository and two data sources (local using Room and a fake remote).
- Dependency injection using [Hilt](https://developer.android.com/training/dependency-injection/hilt-android).
