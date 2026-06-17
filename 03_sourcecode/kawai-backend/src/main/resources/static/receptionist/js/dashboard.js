document.addEventListener("DOMContentLoaded", () => {
    const alertClose = document.querySelector('.close-alert');
    if (alertClose) {
        alertClose.addEventListener('click', (e) => {
            e.target.closest('.alert-warning').style.display = 'none';
        });
    }
});
