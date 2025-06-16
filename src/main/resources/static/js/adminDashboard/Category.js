// $(document).ready(function () {
//     let typingTimer;
//     const debounceDelay = 200;
//     let currentName = $('#searchInput').val() || '';
//     let currentSort = $('#sortSelect').val() || '';
//     let currentPage = 0;

//     // Handle search input with debounce
//     $('#searchInput').on('input', function () {
//         clearTimeout(typingTimer);
//         typingTimer = setTimeout(() => {
//             currentName = $(this).val().trim();
//             currentPage = 0;
//             fetchCategories(currentPage);
//         }, debounceDelay);
//     });

//     // Handle sort change
//     $('#sortSelect').on('change', function () {
//         currentSort = $(this).val();
//         currentPage = 0;
//         fetchCategories(currentPage);
//     });

//     // Handle pagination clicks
//     $(document).on('click', '.pagination .page-link', function (e) {
//         e.preventDefault();
//         const page = $(this).attr('th:data-page');
//         if (page !== undefined) {
//             currentPage = parseInt(page);
//             fetchCategories(currentPage);
//         }
//     });

//     // Handle delete
//     $(document).on('click', '.delete-category', function (e) {
//         e.preventDefault();

//         const id = $(this).data('id');
//         if (!confirm('Bạn có chắc chắn muốn xoá danh mục này?')) {
//             return;
//         }

//         $.ajax({
//             url: '/admin/category/delete',
//             type: 'GET',
//             data: { id: id },
//             headers: {
//                 'X-Requested-With': 'XMLHttpRequest'
//             },
//             success: function () {
//                 fetchCategories(currentPage); // Không reset về trang đầu
//             },
//             error: function (xhr, status, error) {
//                 console.error("Lỗi khi xoá danh mục:", status, error);
//                 alert("Đã xảy ra lỗi khi xoá danh mục.");
//             }
//         });
//     });

//     // Function to fetch categories using AJAX
//     function fetchCategories(page) {
//         $.ajax({
//             url: '/admin/category',
//             type: 'GET',
//             data: {
//                 name: currentName,
//                 sort: currentSort,
//                 page: page,
//                 size: 5
//             },
//             headers: {
//                 'X-Requested-With': 'XMLHttpRequest'
//             },
//             success: function(response) {
//                 $('#categoryPage').html(response);
//                 updateUrl();
//             },
//             error: function(xhr, status, error) {
//                 console.error("Error fetching categories:", status, error);
//                 alert("Error loading categories. Please try again.");
//             }
//         });
//     }

//     // Function to update URL with filter parameters
//     function updateUrl() {
//         const params = new URLSearchParams();
//         if (currentName) params.append('name', currentName);
//         if (currentSort) params.append('sort', currentSort);
//         if (currentPage > 0) params.append('page', currentPage);
        
//         const newUrl = `${window.location.pathname}${params.toString() ? '?' + params.toString() : ''}`;
//         window.history.pushState({}, '', newUrl);
//     }

//     // Initial load
//     fetchCategories(currentPage);
// });
