<script setup>
import {onMounted, onUnmounted, ref} from 'vue';
import {api} from '../services/api';
import CreateEditDialog from '../components/CreateEditDialog.vue';
import {createWebSocket} from '../services/websocket';

const groups = ref([]);
const page = ref(0);
const pageSize = 10;
const filterName = ref('');
const sortField = ref('id');
const sortAsc = ref(true);
const showDialog = ref(false);
const selectedGroup = ref(null);

const totalPages = ref(1);

let stompClient = null;
let debounceTimer = null;

async function fetchGroups() {
  try {
    const res = await api.get('/groups', {
      params: {
        page: page.value,
        size: pageSize,
        nameContains: filterName.value || undefined,
        sort: sortField.value,
        asc: sortAsc.value
      }
    });

    const payload = res.data;
    if (payload && Array.isArray(payload.content)) {
      groups.value = payload.content;
      totalPages.value = payload.totalPages ?? 1;

      if (typeof payload.number === 'number') {
        page.value = payload.number;
      }
    } else if (Array.isArray(payload)) {
      groups.value = payload;
      totalPages.value = Math.ceil(groups.value.length / pageSize) || 1;
    } else {
      groups.value = payload.content ?? [];
      totalPages.value = payload.totalPages ?? 1;
    }

    console.debug('Groups loaded', groups.value);
  } catch (err) {
    console.error('Failed to fetch groups', err);
  }
}

function sortBy(field) {
  if (sortField.value === field) sortAsc.value = !sortAsc.value;
  else {
    sortField.value = field;
    sortAsc.value = true;
  }
  fetchGroups();
}

function nextPage() {
  if (page.value + 1 < totalPages.value) {
    page.value++;
    fetchGroups();
  }
}

function prevPage() {
  if (page.value > 0) {
    page.value--;
    fetchGroups();
  }
}

function editGroup(group) {
  selectedGroup.value = {...group};
  showDialog.value = true;
}

function openCreateDialog() {
  selectedGroup.value = null;
  showDialog.value = true;
}

function closeDialog() {
  showDialog.value = false;
}

async function deleteGroup(id) {
  try {
    await api.delete(`/groups/${id}`);
    await fetchGroups();
  } catch (err) {
    console.error('Delete failed', err);
    alert('Delete failed: ' + (err.response?.data?.message ?? err.message));
  }
}

function handleWsMessage(payload) {
  if (!payload || !payload.event) return;

  switch (payload.event) {
    case 'created':
    case 'updated':
    case 'deleted':
      if (debounceTimer) {
        clearTimeout(debounceTimer);
      }

      debounceTimer = setTimeout(() => {
        fetchGroups();
        debounceTimer = null;
      }, 250);
      break;

    default:
      console.warn('Unknown WS event', payload);
  }
}

onMounted(() => {
  fetchGroups();

  stompClient = createWebSocket((payload) => {
    handleWsMessage(payload);
  });
});

onUnmounted(() => {
  if (stompClient && typeof stompClient.deactivate === 'function') {
    stompClient.deactivate();
    stompClient = null;
  }
});

function onFilterInput() {
  if (debounceTimer) clearTimeout(debounceTimer);
  debounceTimer = setTimeout(() => {
    page.value = 0;
    fetchGroups();
    debounceTimer = null;
  }, 300);
}

function onSaved() {
  showDialog.value = false;
  fetchGroups();
}
</script>

<template>
  <div class="container">
    <h1>Study Groups</h1>
    <input v-model="filterName" placeholder="Filter by name..." @input="onFilterInput" class="filter-input"/>

    <table class="group-table">
      <thead>
      <tr>
        <th @click="sortBy('id')">ID</th>
        <th @click="sortBy('name')">Name</th>
        <th>Students Count</th>
        <th>Admin</th>
        <th></th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="group in groups" :key="group.id">
        <td>{{ group.id }}</td>
        <td>{{ group.name }}</td>
        <td>{{ group.studentsCount }}</td>
        <td>{{ group.groupAdmin?.name || '—' }}</td>
        <td>
          <div class="action-buttons">
            <button class="edit-btn" @click="editGroup(group)">Edit</button>
            <button class="delete-btn" @click="deleteGroup(group.id)">Delete</button>
          </div>
        </td>
      </tr>
      </tbody>
    </table>

    <div class="pagination">
      <button @click="prevPage" :disabled="page===0">Prev</button>
      <span>Page {{ page + 1 }} / {{ totalPages }}</span>
      <button @click="nextPage" :disabled="page + 1 >= totalPages">Next</button>
    </div>

    <button class="create-btn" @click="openCreateDialog">Create New Group</button>

    <CreateEditDialog
        v-if="showDialog"
        :group="selectedGroup"
        @close="closeDialog"
        @saved="onSaved"
    />
  </div>
</template>

<style scoped>
.container {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  padding: 1rem;
  max-width: 900px;
}

h1 {
  margin-bottom: 1rem;
}

.filter-input {
  padding: 0.5rem;
  margin-bottom: 1rem;
  width: 100%;
  max-width: 300px;
  border-radius: 4px;
  border: 1px solid #ccc;
}

.group-table {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 1rem;
  background-color: #fff;
  box-shadow: 0 2px 5px rgba(0,0,0,0.1);
}

.group-table th, .group-table td {
  padding: 0.75rem 1rem;
  border: 1px solid #ddd;
  text-align: center;
}

.group-table thead th {
  background-color: #f0f0f0;
  cursor: pointer;
}

.group-table tbody tr:hover {
  background-color: #f5faff;
}

.action-buttons {
  display: flex;
  justify-content: space-around;
}

button {
  border: none;
  border-radius: 4px;
  padding: 0.35rem 0.75rem;
  font-size: 0.9rem;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.edit-btn {
  background-color: #2196F3;
  color: white;
}

.edit-btn:hover {
  background-color: #1976d2;
}

.delete-btn {
  background-color: #f44336;
  color: white;
}

.delete-btn:hover {
  background-color: #d32f2f;
}

.create-btn {
  background-color: #4CAF50;
  color: white;
  margin-top: 0.5rem;
}

.create-btn:hover {
  background-color: #45a049;
}

.pagination {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 1rem;
}
</style>
