// function initCategoryDropdown(allCategories) {
//     var viewAllBtn = document.getElementById('viewAllCategoriesBtn');
//     var dropdown = document.getElementById('categoryDropdown');

//     if (!viewAllBtn || !dropdown || !Array.isArray(allCategories)) return;

//     viewAllBtn.addEventListener('click', function (e) {
//         e.preventDefault();

//         // Xóa nội dung dropdown
//         dropdown.innerHTML = '';

//         // Hiển thị tất cả danh mục
//         allCategories.forEach(function (category) {
//             var li = document.createElement('li');
//             var a = document.createElement('a');
//             a.className = 'dropdown-item';
//             a.href = '#';
//             a.textContent = category.name;
//             li.appendChild(a);
//             dropdown.appendChild(li);
//         });

//         // Thêm đường kẻ phân cách
//         var divider = document.createElement('li');
//         divider.innerHTML = '<hr class="dropdown-divider">';
//         dropdown.appendChild(divider);

//         // Thêm nút Collapse
//         var collapseItem = document.createElement('li');
//         var collapseLink = document.createElement('a');
//         collapseLink.className = 'dropdown-item text-danger';
//         collapseLink.href = '#';
//         collapseLink.textContent = 'Collapse';
//         collapseLink.addEventListener('click', function (e) {
//             e.preventDefault();
//             location.reload();
//         });
//         collapseItem.appendChild(collapseLink);
//         dropdown.appendChild(collapseItem);
//     });
// }


// // Gọi hàm sau khi DOM đã tải xong
// document.addEventListener('DOMContentLoaded', function () {
//     // Gọi hàm khởi tạo với biến allCategories từ Thymeleaf hoặc API
//     initCategoryDropdown(typeof allCategories !== 'undefined' ? allCategories : []);

// });




