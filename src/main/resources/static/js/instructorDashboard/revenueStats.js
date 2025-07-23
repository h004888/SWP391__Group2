// revenueStats.js - Handles Revenue Statistics tab for instructor dashboard

$(document).ready(function () {
    let revenueChart, courseCompareChart;

    function fetchRevenueStats(range) {
        // Placeholder AJAX - replace with your real endpoint
        return $.ajax({
            url: '/instructor/revenue/statistics',
            method: 'GET',
            data: { range: range },
        });
    }

    function fetchCourseSales() {
        // Placeholder AJAX - replace with your real endpoint
        return $.ajax({
            url: '/instructor/revenue/course-sales',
            method: 'GET',
        });
    }

    function renderRevenueChart(labels, data) {
        const ctx = document.getElementById('revenueChart').getContext('2d');
        if (revenueChart) revenueChart.destroy();
        revenueChart = new Chart(ctx, {
            type: 'line',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Revenue',
                    data: data,
                    borderColor: '#007bff',
                    backgroundColor: 'rgba(0,123,255,0.1)',
                    fill: true,
                    tension: 0.3
                }]
            },
            options: {
                responsive: true,
                plugins: { legend: { display: false } },
                scales: {
                    x: {
                        ticks: {
                            maxRotation: 45,
                            minRotation: 30,
                            autoSkip: false,
                            callback: function(value, index, values) {
                                let label = this.getLabelForValue(value);
                                return label.length > 16 ? label.slice(0, 16) + '…' : label;
                            }
                        }
                    },
                    y: { beginAtZero: true }
                }
            }
        });
    }

    function renderCourseCompareChart(labels, data) {
        const ctx = document.getElementById('courseCompareChart').getContext('2d');
        if (courseCompareChart) courseCompareChart.destroy();
        courseCompareChart = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Revenue',
                    data: data,
                    backgroundColor: '#28a745',
                }]
            },
            options: {
                responsive: true,
                plugins: { legend: { display: false } },
                scales: {
                    x: {
                        ticks: {
                            maxRotation: 45,
                            minRotation: 30,
                            autoSkip: false,
                            callback: function(value, index, values) {
                                let label = this.getLabelForValue(value);
                                return label.length > 16 ? label.slice(0, 16) + '…' : label;
                            }
                        }
                    },
                    y: { beginAtZero: true }
                }
            }
        });
    }

    function renderCourseSalesTable(courses) {
        const tbody = $('#courseSalesTable');
        tbody.empty();
        courses.forEach(course => {
            tbody.append(`<tr><td style="max-width:220px;white-space:normal;word-break:break-word;">${course.title}</td><td>${course.sold}</td><td>${course.revenue.toLocaleString()}</td></tr>`);
        });
    }

    function renderBestLeastSeller(courses) {
        if (!courses.length) {
            $('#bestSeller').text('N/A');
            $('#leastSeller').text('N/A');
            return;
        }
        let sorted = [...courses].sort((a, b) => b.sold - a.sold);
        $('#bestSeller').text(`${sorted[0].title} (${sorted[0].sold} sold)`);
        $('#leastSeller').text(`${sorted[sorted.length-1].title} (${sorted[sorted.length-1].sold} sold)`);
    }

    function loadRevenueTab() {
        const range = $('#revenueRange').val();
        // Fetch and render revenue chart
        fetchRevenueStats(range).done(function (res) {
            // res: { labels: [...], data: [...] }
            renderRevenueChart(res.labels, res.data);
        });
        // Fetch and render course sales table and comparison chart
        fetchCourseSales().done(function (res) {
            // res: [{title, sold, revenue}, ...]
            renderCourseSalesTable(res);
            renderBestLeastSeller(res);
            renderCourseCompareChart(res.map(c => c.title), res.map(c => c.revenue));
        });
    }

    // Initial load when tab is shown
    $('button#revenue-tab').on('shown.bs.tab', function () {
        loadRevenueTab();
    });
    // Also reload when range changes
    $('#revenueRange').on('change', function () {
        loadRevenueTab();
    });
    // If tab is already active on page load
    if ($('button#revenue-tab').hasClass('active')) {
        loadRevenueTab();
    }
}); 