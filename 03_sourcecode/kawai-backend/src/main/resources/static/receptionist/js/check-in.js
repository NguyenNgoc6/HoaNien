function showSearchResult() {
    // Show the result section mock
    document.getElementById('searchResultSection').style.display = 'block';
    
    // Add one empty dependent row by default if none exists
    const tbody = document.getElementById('dependentTableBody');
    if (tbody.children.length === 0) {
        addDependentRow();
    }
}

function addDependentRow() {
    const tbody = document.getElementById('dependentTableBody');
    const tr = document.createElement('tr');
    tr.innerHTML = `
        <td><input type="text" class="form-control" placeholder="Full Name"></td>
        <td><input type="text" class="form-control" placeholder="ID / Passport"></td>
        <td><input type="date" class="form-control"></td>
        <td>
            <button type="button" class="btn-danger" onclick="this.closest('tr').remove()"><i class="fa-solid fa-trash"></i></button>
        </td>
    `;
    tbody.appendChild(tr);
}

function proceedToVerify() {
    alert("Proceeding to Verify & Assign step...");
    // Here we would submit the form and navigate to step 2
}
