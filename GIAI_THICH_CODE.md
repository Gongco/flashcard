# HƯỚNG DẪN GIẢI THÍCH MÃ NGUỒN - DỰ ÁN LEARNFLASH (FLASHCARD)

Tài liệu này giải thích chi tiết toàn bộ kiến trúc, cấu trúc thư mục, các lớp chính và các thuật toán cốt lõi trong ứng dụng LearnFlash để giúp bạn tự tin trả lời vấn đáp bảo vệ đồ án.

---

## 1. KIẾN TRÚC TỔNG QUAN (MVVM + REPOSITORY)

Dự án áp dụng mô hình kiến trúc chuẩn khuyến nghị của Google: **MVVM (Model - View - ViewModel)** kết hợp với **Repository Pattern** để quản lý dữ liệu.

```
┌────────────────────────────────────────────────────────┐
│                      VIEW (UI)                         │
│   (Jetpack Compose: Screens, Components, Theme)       │
└──────────────────────────┬─────────────────────────────┘
                           │ (Quan sát State qua StateFlow)
                           ▼
┌────────────────────────────────────────────────────────┐
│                     VIEWMODEL                          │
│   (FlashcardViewModel - Quản lý State & Logic)        │
└──────────────────────────┬─────────────────────────────┘
                           │ (Gọi hàm lấy/lưu dữ liệu)
                           ▼
┌────────────────────────────────────────────────────────┐
│                    REPOSITORY                          │
│   (FlashcardRepository - Trung gian dữ liệu)           │
└─────────────┬──────────────────────────┬───────────────┘
              │                          │
              ▼                          ▼
┌──────────────────────────┐      ┌──────────────────────┐
│     LOCAL DATABASE       │      │  REMOTE DATA SOURCE  │
│  (Room: Sqlite Local)    │      │ (Retrofit - nếu có)  │
└──────────────────────────┘      └──────────────────────┘
```

* **Model:** Các lớp dữ liệu thô (Data class) đại diện cho bảng trong Database.
* **View:** Giao diện người dùng được xây dựng hoàn toàn bằng **Jetpack Compose** (khai báo dạng hàm `@Composable`), không dùng file XML truyền thống.
* **ViewModel:** Cầu nối giữ trạng thái UI (State) sống sót qua các lần thay đổi cấu hình (xoay màn hình).
* **Repository:** Đóng vai trò là nguồn dữ liệu duy nhất (Single Source of Truth), gom dữ liệu từ database cục bộ để cung cấp cho ViewModel.

---

## 2. CẤU TRÚC THƯ MỤC CHÍNH

* `com.example.flashcard`
  * 📁 `data`
    * 📁 `local`: Chứa cấu hình database Room (`AppDatabase.kt`) và các lớp truy vấn dữ liệu (`UserDao.kt`, `DeckDao.kt`, `FlashcardDao.kt`).
    * 📁 `repository`: Chứa `FlashcardRepository.kt` điều phối dữ liệu.
  * 📁 `model`: Định nghĩa thực thể dữ liệu (`User.kt`, `Deck.kt`, `Flashcard.kt`, `Screen.kt`).
  * 📁 `ui`
    * 📁 `components`: Các thành phần giao diện dùng chung (`CommonComponents.kt` như Button, TextField, Thẻ lật, Nút đổi giao diện).
    * 📁 `screens`: Các màn hình chính (`LoginScreen.kt`, `RegisterScreen.kt`, `HomeScreen.kt`, `DeckDetailScreen.kt`, `AddCardScreen.kt`, `ReviewScreen.kt`, `StatsScreen.kt`).
    * 📁 `theme`: Định nghĩa màu sắc, font chữ và cấu hình chủ đề tối/sáng (`Color.kt`, `Theme.kt`, `Type.kt`).
  * 📁 `viewmodel`: Lớp `FlashcardViewModel.kt` quản lý logic nghiệp vụ.

---

## 3. GIẢI THÍCH CHI TIẾT CÁC THÀNH PHẦN CỐT LÕI

### 3.1. ROOM DATABASE (Lưu trữ cục bộ)
Dự án sử dụng thư viện Room để làm việc với SQLite dễ dàng hơn.
* **`UserDao`, `DeckDao`, `FlashcardDao`:** Định nghĩa các câu lệnh SQL. Ví dụ trong `DeckDao`:
  ```kotlin
  @Query("SELECT * FROM decks WHERE ownerId = :ownerId")
  fun getDecksByOwner(ownerId: Long): Flow<List<Deck>>
  ```
  * **Giải thích:** Hàm này trả về kiểu `Flow`. `Flow` là một dòng truyền dữ liệu bất đồng bộ. Khi dữ liệu trong bảng `decks` thay đổi, Room tự động phát ra danh sách mới và cập nhật lên màn hình mà không cần load lại trang.

### 3.2. THUẬT TOÁN HỌC TẬP (SM-2 Spaced Repetition)
Nằm tại file [Flashcard.kt](file:///d:/Users/sgdgo/AndroidStudioProjects/flashcard/app/src/main/java/com/example/flashcard/model/Flashcard.kt) (Hàm `calculateNextReview`):
* **Easiness Factor (EF - Hệ số dễ):** Giá trị mặc định là `2.5f`. EF càng cao chứng tỏ thẻ càng dễ nhớ. Khi người dùng đánh giá điểm cao (quality = 4 hoặc 5), EF tăng lên. Khi đánh giá điểm thấp, EF giảm đi.
* **Repetition (Số lần lặp đúng liên tiếp):** Tăng lên 1 mỗi khi ôn tập đạt chất lượng $\ge 3$. Nếu điểm $< 3$ (quên thẻ), repetition bị reset về 0.
* **Interval (Khoảng cách ôn tập tiếp theo - tính bằng ngày):**
  * Lần lặp đầu tiên (`repetition == 0`): Ôn tập lại sau `1` ngày.
  * Lần lặp thứ hai (`repetition == 1`): Ôn tập lại sau `6` ngày.
  * Các lần lặp tiếp theo ($n$): Khoảng cách mới = Khoảng cách cũ $\times$ EF (`interval = (interval * easinessFactor).roundToInt()`).
* **Thời gian ôn tiếp theo (`nextReviewAt`):** Bằng thời gian hiện tại cộng thêm số mili-giây tương ứng với `interval` ngày.

### 3.3. HIỆU ỨNG XOAY LẬT THẺ 3D (`FlipCard`)
Nằm tại [CommonComponents.kt](file:///d:/Users/sgdgo/AndroidStudioProjects/flashcard/app/src/main/java/com/example/flashcard/ui/components/CommonComponents.kt):
* Sử dụng `graphicsLayer` để can thiệp vào cách vẽ thẻ:
  ```kotlin
  .graphicsLayer {
      rotationY = rotation // Xoay thẻ theo trục Y nằm ngang
      cameraDistance = 12f * density // Tạo độ sâu không gian 3D, tránh thẻ bị bẹt khi quay
  }
  ```
* Sử dụng `animateFloatAsState` để tự động tính toán các góc quay chuyển tiếp mượt mà từ `0f` (Mặt trước) sang `180f` (Mặt sau).

### 3.4. CHỨC NĂNG ĐỔI THEME DARK/LIGHT (Thủ công & Tự động)
* **Tự động:** File `Theme.kt` sử dụng `isSystemInDarkTheme()` để tự động nhận dạng giao diện của hệ điều hành điện thoại.
* **Thủ công:** 
  * ViewModel sở hữu biến `isDarkTheme` có kiểu `Boolean?` (mặc định là `null` - theo hệ thống).
  * Hàm `toggleTheme(systemDark)` sẽ đảo trạng thái của theme:
    ```kotlin
    fun toggleTheme(systemDark: Boolean) {
        val current = isDarkTheme ?: systemDark
        isDarkTheme = !current
    }
    ```
  * `MainActivity` và `FlashcardApp` lắng nghe biến này để gán giá trị thích hợp vào `FlashcardTheme(darkTheme = isDark)`.
  * Nút `ThemeToggleButton` sử dụng hiệu ứng chuyển động trượt tròn (`animateDpAsState`) và đổi màu nền (`animateColorAsState`) để tạo cảm giác cao cấp.

### 3.5. BẢO MẬT MẬT KHẨU (Hashing & Salt)
Nằm tại [FlashcardRepository.kt](file:///d:/Users/sgdgo/AndroidStudioProjects/flashcard/data/repository/FlashcardRepository.kt) (Hàm `hashPassword` và `verifyPassword`):
* Mật khẩu của người dùng không được lưu dưới dạng văn bản thường (plain text) vì lý do bảo mật.
* Hệ thống tạo ra một chuỗi ngẫu nhiên gọi là **Salt (Muối)** cho từng người dùng, sau đó gộp Salt với mật khẩu thô và băm bằng thuật toán bảo mật **SHA-256** để tạo ra chuỗi **Hash**.
* Khi đăng nhập, hệ thống lấy mật khẩu người dùng vừa nhập, băm với Salt đã lưu trong DB của user đó và đối chiếu xem chuỗi Hash kết quả có trùng với Hash trong DB hay không.

---

## 4. TÓM TẮT THỦ THUẬT TRẢ LỜI VẤN ĐÁP KHI THẦY CÔ CHỈ VÀO UI
Khi thầy/cô chỉ vào bất kỳ thành phần nào trên màn hình và hỏi *"Khi bấm vào đây, luồng code chạy như thế nào?"*, bạn hãy bình tĩnh áp dụng quy tắc 4 bước:

1. **Chỉ file View chứa UI:** *"Thành phần này được định nghĩa ở Composable `<Tên Component>` trong file `<Tên màn hình>Screen.kt`."*
2. **Giải thích Sự kiện click:** *"Khi click, nó sẽ kích hoạt sự kiện `onClick`, gọi tới hàm callback truyền dữ liệu lên."*
3. **Chỉ file ViewModel xử lý logic:** *"Sự kiện này gọi tới hàm `viewModel.<tên hàm>()` trong file `FlashcardViewModel.kt`. Ở đây logic nghiệp vụ được thực hiện trong một Coroutine (luồng phụ) để không gây lag màn hình chính."*
4. **Chỉ Database lưu trữ:** *"ViewModel gọi xuống `repository.<tên hàm>()` để giao tiếp với Room Database qua lớp DAO để ghi/đọc dữ liệu. Sau khi dữ liệu thay đổi, StateFlow trong ViewModel phát dữ liệu mới, UI lắng nghe và tự động vẽ lại (Recompose)."*
