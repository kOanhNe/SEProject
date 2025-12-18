# Use Case [23]: Manage Product

## Actor(s)
- **ADMIN**

## Trigger
Admin truy cập vào mục **“Quản lý sản phẩm”** trong trang quản trị.

## Description
Use Case này cho phép Admin thực hiện các chức năng quản lý sản phẩm giày trong hệ thống, bao gồm:
- Xem danh sách sản phẩm theo phân trang
- Tìm kiếm và lọc sản phẩm
- Thêm mới, chỉnh sửa, xóa hoặc tạm ngừng kinh doanh sản phẩm

Admin có thể cập nhật các thông tin:
- Tên sản phẩm, mã sản phẩm
- Hình ảnh sản phẩm
- Biến thể (màu – size)
- Giá cơ bản
- Danh mục, thương hiệu
- Loại giày (Nam, Nữ, Unisex)
- Mô tả sản phẩm

## Pre-Conditions
- Admin đã đăng nhập vào hệ thống
- Admin có quyền truy cập chức năng quản lý sản phẩm

## Post-Conditions
- Thông tin sản phẩm, hình ảnh, biến thể hoặc trạng thái bán được cập nhật vào cơ sở dữ liệu
- Danh sách sản phẩm hiển thị dữ liệu mới sau khi cập nhật

---

## Main Flow

### 1. Truy cập danh sách sản phẩm
1. Actor truy cập trang **“Quản lý sản phẩm”**  
2. Hệ thống hiển thị danh sách sản phẩm giày theo phân trang mặc định  
3. Danh sách bao gồm các thông tin:
   - Tên sản phẩm
   - Mã sản phẩm
   - Thương hiệu
   - Danh mục
   - Giá
   - Trạng thái bán

---

### 1.A. Phân trang danh sách sản phẩm
1. Hệ thống hiển thị bộ phân trang gồm:
   - Trang trước
   - Trang sau
   - Nhập số trang
   - Chọn số sản phẩm/trang
2. Admin chọn chuyển sang trang khác  
3. Hệ thống gửi yêu cầu truy vấn trang mới  
4. Hệ thống xử lý và trả về danh sách sản phẩm tương ứng  
5. UI cập nhật danh sách sản phẩm theo trang đã chọn  

---

### 2.A. Tìm kiếm và lọc sản phẩm
1. Admin nhập từ khóa vào ô tìm kiếm  
2. (Tùy chọn) Admin chọn thêm bộ lọc:
   - Danh mục
   - Thương hiệu
   - Trạng thái sản phẩm
   - Khoảng giá
3. Admin nhấn **“Tìm kiếm”**  
4. Hệ thống xử lý và hiển thị danh sách sản phẩm phù hợp  
5. Admin chọn sản phẩm để:
   - Xem chi tiết
   - Chỉnh sửa
   - Tạm ngừng bán
   - Xóa  

---

### 2.B. Thêm sản phẩm mới
1. Admin chọn **“Thêm sản phẩm mới”**  
2. Admin nhập các thông tin:
   - Tên sản phẩm
   - Thương hiệu
   - Danh mục
   - Giá cơ bản
   - Mô tả
   - Loại giày
   - Hình ảnh sản phẩm
   - Các biến thể màu – size
3. Hệ thống kiểm tra dữ liệu:
   - Các trường bắt buộc không được để trống
   - SKU không được trùng lặp
   - Biến thể màu – size phải là duy nhất trong cùng sản phẩm
4. Nếu hợp lệ, hệ thống lưu dữ liệu và hiển thị:
   > “Thêm sản phẩm thành công”

---

### 2.C. Chỉnh sửa thông tin sản phẩm
1. Admin chọn sản phẩm và nhấn **“Chỉnh sửa”**  
2. Admin cập nhật:
   - Giá, mô tả
   - Loại giày
   - Hình ảnh
   - Thêm / xóa / chỉnh sửa biến thể màu – size
   - Danh mục, thương hiệu
3. Hệ thống kiểm tra tính hợp lệ  
4. Hệ thống cập nhật và hiển thị thông báo thành công  

---

### 2.D. Tạm ngừng bán sản phẩm
1. Admin chọn sản phẩm và nhấn **“Tạm ngừng bán”**  
2. Hệ thống hiển thị hộp thoại xác nhận  
   > “Bạn có chắc chắn muốn ngừng kinh doanh sản phẩm này không?”
3. Admin nhấn **“Xác nhận”**  
4. Hệ thống cập nhật trạng thái thành **“Ngừng bán”** và hiển thị thông báo  

---

### 2.E. Xóa sản phẩm
1. Admin chọn sản phẩm và nhấn **“Xóa”**  
2. Hệ thống hiển thị hộp thoại xác nhận  
   > “Bạn có chắc chắn muốn xóa sản phẩm này không?”
3. Admin nhấn **“Xác nhận”**  
4. Hệ thống thực hiện **xóa mềm (soft delete)** và ẩn sản phẩm khỏi hệ thống  

---

## Alternate Flow

### AF1. Xóa sản phẩm đã nằm trong đơn hàng đã giao
- Nếu sản phẩm đã xuất hiện trong đơn hàng đã hoàn thành  
- Hệ thống vẫn cho phép xóa mềm  
- Hiển thị thông báo:
  > “Sản phẩm đã được xóa khỏi hệ thống. Khách hàng sẽ không thể xem sản phẩm này nữa.”

### AF2. Chuyển trang vượt quá số trang hiện có
- Nếu Admin yêu cầu trang lớn hơn tổng số trang:
  - Hệ thống hiển thị:
    > “Không có sản phẩm trong trang này. Vui lòng chọn trang khác.”
  - Hệ thống tự động chuyển về trang hợp lệ gần nhất (ví dụ: trang cuối)

---

## Exception Flow

### EF1. Không tìm thấy sản phẩm
- Tại bước **2.A.4**
- Hệ thống hiển thị:
  > “Không tìm thấy sản phẩm phù hợp”

### EF2. Sai dữ liệu khi thêm hoặc chỉnh sửa
- Tại bước **2.B.3** hoặc **2.C.3**
- Nếu thiếu thông tin hoặc sai định dạng:
  - Hệ thống đánh dấu đỏ các trường lỗi
  - Yêu cầu Admin nhập lại

### EF3. Không thể xóa sản phẩm do đang được sử dụng
- Nếu sản phẩm đang nằm trong:
  - Giỏ hàng
  - Đơn hàng đang xử lý / đang giao
- Hệ thống hiển thị:
  > “Không thể xóa sản phẩm do đang được sử dụng. Vui lòng chọn chức năng ‘Tạm ngừng bán’.”
