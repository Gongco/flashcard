# Flashcard

## Setup khi clone project

Project này dùng Android Gradle Plugin 8.x, nên Gradle cần chạy bằng JDK 17 hoặc JDK 21.

Khuyến nghị:

- Mở bằng Android Studio và chọn `Settings > Build, Execution, Deployment > Build Tools > Gradle > Gradle JDK`.
- Chọn `Embedded JDK` hoặc JDK 17/21 đã cài trên máy.
- Không dùng JDK 25 cho project này vì Kotlin Gradle DSL hiện tại có thể không parse được version `25.x`.
- Không commit `local.properties` hoặc cấu hình JDK theo path riêng của máy.

Build thử:

```powershell
.\gradlew.bat testDebugUnitTest
.\gradlew.bat assembleDebug
```
