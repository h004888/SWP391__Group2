let currentStatus = 'PENDING';
let currentPages = {
    PENDING: 0,
    PAID: 0,
    OVERDUE: 0
};

$(document).ready(function () {
    $('.max-enrollments').each(function() {
        const input = this;
        const form = $(input).closest('form');

        if (input.value.trim() === '') {
            input.placeholder = 'MAX';
        } else {
            input.placeholder = '';
        }
        
        updateSaveButtonVisibility(form);
    });

    // Set current date to month year filter
    const today = new Date();
    const currentMonth = String(today.getMonth() + 1).padStart(2, '0');
    const currentYear = today.getFullYear();
    const currentMonthYear = `${currentYear}-${currentMonth}`;
    $('#monthYearFilter').val(currentMonthYear);

    const statuses = ['pending', 'paid', 'overdue'];
    statuses.forEach(status => {
        loadMaintenances(status, 0);
    });

    $('button[data-bs-toggle="tab"]').on('shown.bs.tab', function (e) {
        const status = $(this).data('status');
        console.log("Tab changed to:", status);
        currentStatus = status;
        filterMaintenances(currentStatus, currentPages[currentStatus] || 0);
    });

    // Filter events
    $('#monthYearFilter').on('change', function () {
        console.log("Month year filter changed:", $(this).val());
        currentPages[currentStatus] = 0;
        filterMaintenances(currentStatus, 0);
    });

    let searchTimer;
    $('#searchInput').on('input', function () {
        const searchValue = $(this).val();
        console.log("Search input triggered - value:", searchValue);
        clearTimeout(searchTimer);
        searchTimer = setTimeout(function () {
            currentPages[currentStatus] = 0;
            filterMaintenances(currentStatus, 0);
        }, 500);
    });

    $('#filterForm').on('submit', function (e) {
        e.preventDefault();
        currentPages[currentStatus] = 0;
        filterMaintenances(currentStatus, 0);
    });

    function getFeeRulesFromTable() {
        let rules = [];
        document.querySelectorAll('.fee-form').forEach(form => {
            const minInput = form.querySelector('.min-enrollments').value;
            const maxInput = form.querySelector('.max-enrollments').value;
            
            if (minInput.trim() === '') {
                return;
            }
            
            const min = parseInt(minInput, 10);
            const max = maxInput.trim() === "" ? null : parseInt(maxInput, 10);
            rules.push({min, max, form});
        });
        return rules;
    }

    function validateFeeRule(currentMin, currentMax, rules, currentForm) {
        if (!currentMin || isNaN(currentMin)) {
            return "Min Enrollments cannot be empty!";
        }

        if (currentMin <= 0) {
            return "Min Enrollments must be greater than 0!";
        }

        if (currentMax !== null && currentMin >= currentMax) {
            return "Min Enrollments must be less than Max Enrollments!";
        }

        if (currentMax === null) {
            const otherRulesWithNullMax = rules.filter(r => r.form !== currentForm && r.max === null);
            if (otherRulesWithNullMax.length > 0) {
                const maxMin = Math.max(...otherRulesWithNullMax.map(r => r.min));
                if (currentMin <= maxMin) {
                    return "If Max Enrollments is empty, Min Enrollments must be the largest!";
                }
            }
        }

        // No overlapping ranges
        for (let rule of rules) {
            if (rule.form === currentForm) continue;
            let minA = currentMin, maxA = currentMax, minB = rule.min, maxB = rule.max;
            if (maxA === null) maxA = Infinity;
            if (maxB === null) maxB = Infinity;
            if (Math.max(minA, minB) < Math.min(maxA, maxB)) {
                return "Min-Max range overlaps with another rule!";
            }
        }
        return null;
    }

    // Store original values for comparison
    let originalValues = {};

    function storeOriginalValues() {
        document.querySelectorAll('.fee-form').forEach(form => {
            const feeId = form.querySelector('input[name="feeId"]').value;
            const minValue = form.querySelector('.min-enrollments').value;
            const maxValue = form.querySelector('.max-enrollments').value;
            const maintenanceFeeValue = form.querySelector('.maintenance-fee').value;
            originalValues[feeId] = {
                min: minValue,
                max: maxValue,
                maintenanceFee: maintenanceFeeValue
            };
        });
    }

    function hasChanges(form) {
        const feeId = form.querySelector('input[name="feeId"]').value;
        const currentMin = form.querySelector('.min-enrollments').value;
        const currentMax = form.querySelector('.max-enrollments').value;
        const currentMaintenanceFee = form.querySelector('.maintenance-fee').value;
        
        if (!originalValues[feeId]) return true;
        
        const hasChanges = originalValues[feeId].min !== currentMin || 
               originalValues[feeId].max !== currentMax ||
               originalValues[feeId].maintenanceFee !== currentMaintenanceFee;
        
        return hasChanges;
    }

    document.addEventListener('DOMContentLoaded', function() {
        storeOriginalValues();

        window.hasChanges = hasChanges;

        document.querySelectorAll('.fee-form').forEach(form => {
            const minInput = form.querySelector('.min-enrollments');
            const maxInput = form.querySelector('.max-enrollments');
            const maintenanceFeeInput = form.querySelector('.maintenance-fee');
            
            minInput.addEventListener('input', function() {
                handleInputChange(this);
            });
            
            maxInput.addEventListener('input', function() {
                handleInputChange(this);
            });
            
            maintenanceFeeInput.addEventListener('input', function() {
                handleInputChange(this);
            });
        });
        
        setTimeout(() => {
            document.querySelectorAll('.fee-form').forEach(form => {
                updateSaveButtonVisibility(form);
            });
        }, 100);

        document.querySelectorAll('.fee-form').forEach(form => {
            form.addEventListener('submit', function(e) {
                if (!hasChanges(form)) {
                    e.preventDefault(); // Prevent submission if no changes
                    return;
                }

                const minInput = form.querySelector('.min-enrollments').value;
                const maxInput = form.querySelector('.max-enrollments').value;
                const maintenanceFeeInput = form.querySelector('.maintenance-fee').value;
                
                // Check if min enrollment is empty
                if (minInput.trim() === '') {
                    e.preventDefault();
                    alert("Min Enrollments cannot be empty!");
                    return;
                }

                // Check if maintenance fee is empty
                if (maintenanceFeeInput.trim() === '') {
                    e.preventDefault();
                    alert("Maintenance Fee cannot be empty!");
                    return;
                }

                const min = parseInt(minInput, 10);
                const max = maxInput.trim() === "" ? null : parseInt(maxInput, 10);
                const maintenanceFee = parseInt(maintenanceFeeInput, 10);
                
                // Additional validation
                if (min <= 0) {
                    e.preventDefault();
                    alert("Min Enrollments must be greater than 0!");
                    return;
                }
                
                if (max !== null && max <= 0) {
                    e.preventDefault();
                    alert("Max Enrollments must be greater than 0!");
                    return;
                }
                
                if (maintenanceFee <= 0) {
                    e.preventDefault();
                    alert("Maintenance Fee must be greater than 0!");
                    return;
                }
                
                const rules = getFeeRulesFromTable();
                const error = validateFeeRule(min, max, rules, form);
                if (error) {
                    e.preventDefault();
                    alert(error);
                }
            });
        });

        // Validate on add
        const addForm = document.getElementById('addFeeForm');
        if (addForm) {
            addForm.addEventListener('submit', function(e) {
                const minInput = addForm.querySelector('input[name="minEnrollments"]').value;
                const maxInput = addForm.querySelector('input[name="maxEnrollments"]').value;
                const maintenanceFeeInput = addForm.querySelector('input[name="maintenanceFee"]').value;
                
                // Check if min enrollment is empty
                if (minInput.trim() === '') {
                    e.preventDefault();
                    alert("Min Enrollments cannot be empty!");
                    return;
                }

                // Check if maintenance fee is empty
                if (maintenanceFeeInput.trim() === '') {
                    e.preventDefault();
                    alert("Maintenance Fee cannot be empty!");
                    return;
                }

                const min = parseInt(minInput, 10);
                const max = maxInput.trim() === "" ? null : parseInt(maxInput, 10);
                const maintenanceFee = parseInt(maintenanceFeeInput, 10);
                
                // Additional validation
                if (min <= 0) {
                    e.preventDefault();
                    alert("Min Enrollments must be greater than 0!");
                    return;
                }
                
                if (max !== null && max <= 0) {
                    e.preventDefault();
                    alert("Max Enrollments must be greater than 0!");
                    return;
                }
                
                if (maintenanceFee <= 0) {
                    e.preventDefault();
                    alert("Maintenance Fee must be greater than 0!");
                    return;
                }
                
                const rules = getFeeRulesFromTable();
                const error = validateFeeRule(min, max, rules, null);
                if (error) {
                    e.preventDefault();
                    alert(error);
                }
            });
        }
    });
});

// Function to handle max enrollment input changes
function handleMaxEnrollmentChange(input) {
    const form = $(input).closest('form');
    const saveBtn = form.find('.save-btn');

    if (input.value.trim() === '') {
        input.placeholder = 'MAX';
    } else {
        input.placeholder = '';
    }
    
    // Show/hide save button based on changes
    updateSaveButtonVisibility(form);
}

// Function to update save button visibility based on changes
function updateSaveButtonVisibility(form) {
    const saveBtn = $(form).find('.save-btn');
    const hasChanges = window.hasChanges ? window.hasChanges(form) : true;
    
    if (hasChanges) {
        saveBtn.show();
    } else {
        saveBtn.hide();
    }
}

// Function to handle input changes for all fields
function handleInputChange(input) {
    const form = $(input).closest('form');
    updateSaveButtonVisibility(form);
}

// Function to handle fee deletion
function deleteFee(button) {
    if (confirm('Are you sure you want to delete this fee rule?')) {
        const feeId = $(button).data('fee-id');
        const form = $('<form>', {
            'method': 'POST',
            'action': '/admin/courseMaintenance/fees/delete'
        });

        form.append($('<input>', {
            'type': 'hidden',
            'name': 'feeId',
            'value': feeId
        }));

        $('body').append(form);
        form.submit();
    }
}

// Auto-hide alerts after 3 seconds
setTimeout(function () {
    $('#success-alert').fadeOut('slow');
    $('#error-alert').fadeOut('slow');
}, 5000);

function getTableBodyElement(status) {
    switch (status) {
        case 'pending':
            return $('#pendingTableBody');
        case 'paid':
            return $('#paidTableBody');
        case 'overdue':
            return $('#overdueTableBody');
        default:
            return $('#pendingTableBody');
    }
}

function getPaginationElement(status) {
    switch (status) {
        case 'pending':
            return $('#pendingPagination');
        case 'paid':
            return $('#paidPagination');
        case 'overdue':
            return $('#overduePagination');
        default:
            return $('#pendingPagination');
    }
}

function filterMaintenances(status, page = 0, size = 10) {
    let username = $('#searchInput').val() ? $('#searchInput').val().trim() : '';
    let monthYear = $('#monthYearFilter').val() || '';

    let tableBody = getTableBodyElement(status);
    let paginationContainer = getPaginationElement(status);

    if (!tableBody || tableBody.length === 0) {
        console.error("Table body not found for status:", status);
        return;
    }

    // Update current page for this status
    currentPages[status] = page;

    // Show loading state
    tableBody.html('<tr><td colspan="9" class="text-center">Loading...</td></tr>');

    // Load table data and pagination simultaneously
    Promise.all([
        // Load table rows
        $.ajax({
            url: '/admin/courseMaintenance/filter',
            method: 'GET',
            data: {
                username: username || null,
                status: status,
                monthYear: monthYear || null,
                page: page,
                size: size
            }
        }),
        // Load pagination
        $.ajax({
            url: '/admin/courseMaintenance/pagination',
            method: 'GET',
            data: {
                username: username || null,
                status: status,
                monthYear: monthYear || null,
                page: page,
                size: size
            }
        }),
        // Load total count for badge
        $.ajax({
            url: '/admin/courseMaintenance/count',
            method: 'GET',
            data: {
                username: username || null,
                status: status,
                monthYear: monthYear || null
            }
        })
    ]).then(function ([tableData, paginationData, countData]) {

        // Update table
        if (tableData && tableData.trim() !== '') {
            tableBody.html(tableData);
        } else {
            tableBody.html('<tr><td colspan="9" class="text-center">No maintenances found</td></tr>');
        }

        // Update pagination
        if (paginationData && paginationData.trim() !== '') {
            paginationContainer.html(paginationData);
        } else {
            paginationContainer.html('');
        }

        // Update count badge
        updateStatusCountBadge(status, countData);

    }).catch(function (error) {
        console.error("Error loading data for status:", status, error);
        tableBody.html('<tr><td colspan="9" class="text-center text-danger">Error loading data</td></tr>');
        paginationContainer.html('');
    });
}

function updateStatusCountBadge(status, totalCount) {
    const statusMap = {
        'pending': 'pendingCount',
        'paid': 'paidCount',
        'overdue': 'overdueCount'
    };

    const badgeId = statusMap[status];
    const badgeElement = document.getElementById(badgeId);

    if (!badgeElement) {
        console.error("Badge element not found for status:", status);
        return;
    }

    // Set the total count from server response
    const count = typeof totalCount === 'number' ? totalCount : (totalCount ? parseInt(totalCount) : 0);
    badgeElement.textContent = count;
}

function loadMaintenances(status, page = 0) {
    console.log("Loading maintenances for status:", status, "page:", page);
    filterMaintenances(status, page);
}

// Event handler for pagination clicks
$(document).on('click', '.pagination .page-link', function (e) {
    e.preventDefault();
    const page = parseInt($(this).data('page'));
    if (!isNaN(page) && page >= 0) {
        console.log("Pagination clicked - page:", page, "for status:", currentStatus);
        filterMaintenances(currentStatus, page);
    }
}); 