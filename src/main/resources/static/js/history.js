// Configuration object for easy maintenance
const CONFIG = {
    minAmount: 10000,
    depositColor: '#007bff',
    withdrawColor: '#dc3545',
    defaultBorderColor: '#ddd'
};

// Utility functions
const Utils = {
    clearAmountSelection(optionClass, predefinedFieldId, customFieldId) {
        document.querySelectorAll(optionClass).forEach(option => {
            option.style.backgroundColor = '';
            option.style.borderColor = CONFIG.defaultBorderColor;
            option.style.color = '';
        });
        document.getElementById(predefinedFieldId).value = '';
        document.getElementById(customFieldId).value = '';
    },

    selectAmount(element, optionClass, predefinedFieldId, customFieldId, color, validateFunction) {
        // Clear all options
        document.querySelectorAll(optionClass).forEach(option => {
            option.style.backgroundColor = '';
            option.style.borderColor = CONFIG.defaultBorderColor;
            option.style.color = '';
        });

        // Highlight selected option
        element.style.backgroundColor = color;
        element.style.borderColor = color;
        element.style.color = 'white';

        // Set values
        const amount = element.getAttribute('data-amount');
        document.getElementById(predefinedFieldId).value = amount;
        document.getElementById(customFieldId).value = '';

        // Validate if function provided
        if (validateFunction) validateFunction();
    },

    handleCustomInput(customFieldId, predefinedFieldId, optionClass, validateFunction) {
        const customAmount = document.getElementById(customFieldId).value;
        if (customAmount) {
            document.getElementById(predefinedFieldId).value = '';
            document.querySelectorAll(optionClass).forEach(option => {
                option.style.backgroundColor = '';
                option.style.borderColor = CONFIG.defaultBorderColor;
                option.style.color = '';
            });
        }
        if (validateFunction) validateFunction();
    },

    // Simplified validation - only basic input validation for UX
    validateAmountInput(customFieldId, predefinedFieldId, errorFieldId, minAmount) {
        const customAmount = document.getElementById(customFieldId).value;
        const predefinedAmount = document.getElementById(predefinedFieldId).value;
        const errorElement = document.getElementById(errorFieldId);

        // Clear previous error
        errorElement.style.display = 'none';

        // Check if either amount is provided
        if ((!customAmount || customAmount.trim() === '') &&
            (!predefinedAmount || predefinedAmount.trim() === '')) {
            return true; // Let server handle empty validation
        }

        // Determine amount to validate
        let amount;
        if (predefinedAmount && predefinedAmount.trim() !== '') {
            amount = parseFloat(predefinedAmount);
        } else if (customAmount && customAmount.trim() !== '') {
            amount = parseFloat(customAmount);
        } else {
            return true;
        }

        // Only validate basic input format
        if (isNaN(amount)) {
            errorElement.textContent = 'Invalid amount. Please enter a valid number.';
            errorElement.style.display = 'block';
            return false;
        }

        return true;
    },

    toggleForm(formId, overlayId, otherFormId) {
        const form = document.getElementById(formId);
        const overlay = document.getElementById(overlayId);
        const otherForm = document.getElementById(otherFormId);

        if (form.style.display === 'none') {
            form.style.display = 'block';
            overlay.style.display = 'block';
            if (otherForm) otherForm.style.display = 'none';
        } else {
            form.style.display = 'none';
            overlay.style.display = 'none';
        }
    }
};

// Deposit functions
function selectDepositAmount(element) {
    Utils.selectAmount(element, '.amount-option', 'predefinedAmount', 'depositAmount', CONFIG.depositColor, validateDepositAmountInput);
}

function handleCustomDepositInput() {
    Utils.handleCustomInput('depositAmount', 'predefinedAmount', '.amount-option', validateDepositAmountInput);
}

function validateDepositAmountInput() {
    return Utils.validateAmountInput('depositAmount', 'predefinedAmount', 'depositAmountError', CONFIG.minAmount);
}

function validateDepositForm() {
    return Utils.validateAmountInput('depositAmount', 'predefinedAmount', 'depositAmountError', CONFIG.minAmount);
}

function toggleDepositForm() {
    Utils.toggleForm('depositForm', 'formOverlay', 'withdrawForm');
}

// Withdraw functions
function selectWithdrawAmount(element) {
    Utils.selectAmount(element, '.withdraw-amount-option', 'predefinedWithdrawAmount', 'withdrawAmount', CONFIG.withdrawColor, validateWithdrawAmountInput);
}

function handleCustomWithdrawInput() {
    Utils.handleCustomInput('withdrawAmount', 'predefinedWithdrawAmount', '.withdraw-amount-option', validateWithdrawAmountInput);
}

function validateWithdrawAmountInput() {
    return Utils.validateAmountInput('withdrawAmount', 'predefinedWithdrawAmount', 'withdrawAmountError', CONFIG.minAmount);
}

function validateWithdrawForm() {
    return Utils.validateAmountInput('withdrawAmount', 'predefinedWithdrawAmount', 'withdrawAmountError', CONFIG.minAmount);
}

function toggleWithdrawForm() {
    Utils.toggleForm('withdrawForm', 'formOverlay', 'depositForm');
}

// Document ready
$(document).ready(function () {
    // Event handlers
    $('#formOverlay').on('click', function() {
        $('#depositForm, #withdrawForm').hide();
        $(this).hide();
    });
});