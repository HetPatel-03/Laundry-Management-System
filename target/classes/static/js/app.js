// API base URL
const API_URL = '/api/laundry';

// Bootstrap component references
let laundryModal;
let statusModal;
let confirmationModal;
let toast;

// Initialize the application
document.addEventListener('DOMContentLoaded', function() {
  // Initialize Bootstrap components
  laundryModal = new bootstrap.Modal(document.getElementById('laundryModal'));
  statusModal = new bootstrap.Modal(document.getElementById('statusModal'));
  confirmationModal = new bootstrap.Modal(document.getElementById('confirmationModal'));
  toast = new bootstrap.Toast(document.getElementById('toast'));
  
  // Load all laundry orders
  loadLaundries();
  
  // Set up event listeners
  document.getElementById('confirmDeleteButton').addEventListener('click', confirmDelete);
});

// Show loading spinner
function showLoading() {
  document.getElementById('loading').style.display = 'flex';
}

// Hide loading spinner
function hideLoading() {
  document.getElementById('loading').style.display = 'none';
}

// Show toast notification
function showToast(title, message, isError = false) {
  const toastEl = document.getElementById('toast');
  document.getElementById('toastTitle').textContent = title;
  document.getElementById('toastMessage').textContent = message;
  
  if (isError) {
    toastEl.classList.add('bg-danger', 'text-white');
  } else {
    toastEl.classList.remove('bg-danger', 'text-white');
  }
  
  toast.show();
}

// Format date for display
function formatDate(dateString) {
  if (!dateString) return 'N/A';
  const date = new Date(dateString);
  return date.toLocaleString();
}

// Load all laundry orders
async function loadLaundries() {
  showLoading();
  try {
    const response = await fetch(API_URL);
    const result = await response.json();
    
    if (result.success) {
      displayLaundries(result.data);
    } else {
      showToast('Error', result.message || 'Failed to load laundry orders', true);
    }
  } catch (error) {
    console.error('Error loading laundries:', error);
    showToast('Error', 'Failed to connect to the server', true);
  } finally {
    hideLoading();
  }
}

// Display laundry orders in the table
function displayLaundries(laundries) {
  const tableBody = document.getElementById('laundryTable');
  tableBody.innerHTML = '';
  
  if (laundries.length === 0) {
    tableBody.innerHTML = '<tr><td colspan="9" class="text-center">No laundry orders found</td></tr>';
    return;
  }
  
  laundries.forEach(laundry => {
    const row = document.createElement('tr');
    
    row.innerHTML = `
      <td>${laundry.id}</td>
      <td>${laundry.customerName}</td>
      <td>${laundry.phoneNumber}</td>
      <td>${laundry.numberOfItems}</td>
      <td>${laundry.weight}</td>
      <td>$${laundry.price.toFixed(2)}</td>
      <td><span class="badge status-${laundry.status} status-badge">${laundry.status}</span></td>
      <td>${formatDate(laundry.createdAt)}</td>
      <td>
        <button class="btn btn-sm btn-primary btn-action" onclick="viewLaundry(${laundry.id})">Edit</button>
        <button class="btn btn-sm btn-info btn-action" onclick="showStatusModal(${laundry.id}, '${laundry.status}')">Status</button>
        <button class="btn btn-sm btn-danger btn-action" onclick="deleteLaundry(${laundry.id})">Delete</button>
      </td>
    `;
    
    tableBody.appendChild(row);
  });
}

// Show create laundry form
function showCreateForm() {
  document.getElementById('modalTitle').textContent = 'Add New Laundry Order';
  document.getElementById('laundryForm').reset();
  document.getElementById('laundryId').value = '';
  document.getElementById('statusDiv').style.display = 'none';
  document.getElementById('saveButton').onclick = saveLaundry;
  laundryModal.show();
}

// Show edit laundry form
async function viewLaundry(id) {
  showLoading();
  try {
    const response = await fetch(`${API_URL}/${id}`);
    const result = await response.json();
    
    if (result.success) {
      const laundry = result.data;
      document.getElementById('modalTitle').textContent = 'Edit Laundry Order';
      document.getElementById('laundryId').value = laundry.id;
      document.getElementById('customerName').value = laundry.customerName;
      document.getElementById('phoneNumber').value = laundry.phoneNumber;
      document.getElementById('numberOfItems').value = laundry.numberOfItems;
      document.getElementById('weight').value = laundry.weight;
      document.getElementById('price').value = laundry.price;
      document.getElementById('serviceType').value = laundry.serviceType || 'Wash & Fold';
      document.getElementById('specialInstructions').value = laundry.specialInstructions || '';
      document.getElementById('status').value = laundry.status;
      document.getElementById('statusDiv').style.display = 'block';
      
      laundryModal.show();
    } else {
      showToast('Error', result.message || 'Failed to load laundry details', true);
    }
  } catch (error) {
    console.error('Error loading laundry details:', error);
    showToast('Error', 'Failed to connect to the server', true);
  } finally {
    hideLoading();
  }
}

// Save (create or update) laundry
async function saveLaundry() {
  const id = document.getElementById('laundryId').value;
  const isUpdate = !!id;
  
  const laundryData = {
    customerName: document.getElementById('customerName').value,
    phoneNumber: document.getElementById('phoneNumber').value,
    numberOfItems: parseInt(document.getElementById('numberOfItems').value),
    weight: parseFloat(document.getElementById('weight').value),
    price: parseFloat(document.getElementById('price').value),
    serviceType: document.getElementById('serviceType').value,
    specialInstructions: document.getElementById('specialInstructions').value
  };
  
  if (isUpdate) {
    laundryData.status = document.getElementById('status').value;
  }
  
  showLoading();
  try {
    const url = isUpdate ? `${API_URL}/${id}` : API_URL;
    const method = isUpdate ? 'PUT' : 'POST';
    
    const response = await fetch(url, {
      method: method,
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(laundryData)
    });
    
    const result = await response.json();
    
    if (result.success) {
      laundryModal.hide();
      showToast('Success', `Laundry order ${isUpdate ? 'updated' : 'created'} successfully`);
      loadLaundries();
    } else {
      showToast('Error', result.message || `Failed to ${isUpdate ? 'update' : 'create'} laundry order`, true);
    }
  } catch (error) {
    console.error(`Error ${isUpdate ? 'updating' : 'creating'} laundry:`, error);
    showToast('Error', 'Failed to connect to the server', true);
  } finally {
    hideLoading();
  }
}

// Show status update modal
function showStatusModal(id, currentStatus) {
  document.getElementById('statusLaundryId').value = id;
  document.getElementById('newStatus').value = currentStatus;
  statusModal.show();
}

// Update laundry status
async function updateStatus() {
  const id = document.getElementById('statusLaundryId').value;
  const status = document.getElementById('newStatus').value;
  
  showLoading();
  try {
    const response = await fetch(`${API_URL}/${id}/status?status=${status}`, {
      method: 'PATCH'
    });
    
    const result = await response.json();
    
    if (result.success) {
      statusModal.hide();
      showToast('Success', 'Status updated successfully');
      loadLaundries();
    } else {
      showToast('Error', result.message || 'Failed to update status', true);
    }
  } catch (error) {
    console.error('Error updating status:', error);
    showToast('Error', 'Failed to connect to the server', true);
  } finally {
    hideLoading();
  }
}

// Show delete confirmation modal
function deleteLaundry(id) {
  document.getElementById('confirmDeleteButton').setAttribute('data-id', id);
  confirmationModal.show();
}

// Confirm and execute delete
async function confirmDelete() {
  const id = document.getElementById('confirmDeleteButton').getAttribute('data-id');
  
  showLoading();
  try {
    const response = await fetch(`${API_URL}/${id}`, {
      method: 'DELETE'
    });
    
    const result = await response.json();
    
    if (result.success) {
      confirmationModal.hide();
      showToast('Success', 'Laundry order deleted successfully');
      loadLaundries();
    } else {
      showToast('Error', result.message || 'Failed to delete laundry order', true);
    }
  } catch (error) {
    console.error('Error deleting laundry:', error);
    showToast('Error', 'Failed to connect to the server', true);
  } finally {
    hideLoading();
  }
}