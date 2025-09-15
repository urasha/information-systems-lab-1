<script setup>
import {ref, onMounted, onUnmounted} from 'vue';
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
  <div>
    <h1>Study Groups</h1>
    <input v-model="filterName" placeholder="Filter by name..." @input="onFilterInput"/>

    <table>
      <thead>
      <tr>
        <th @click="sortBy('id')">ID</th>
        <th @click="sortBy('name')">Name</th>
        <th>Students Count</th>
        <th>Admin</th>
        <th>Actions</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="group in groups" :key="group.id">
        <td>{{ group.id }}</td>
        <td>{{ group.name }}</td>
        <td>{{ group.studentsCount }}</td>
        <td>{{ group.groupAdmin?.name || '—' }}</td>
        <td>
          <button @click="editGroup(group)">Edit</button>
          <button @click="deleteGroup(group.id)">Delete</button>
        </td>
      </tr>
      </tbody>
    </table>

    <div style="margin-top: 0.5rem;">
      <button @click="prevPage" :disabled="page===0">Prev</button>
      <span style="margin:0 1rem;">Page {{ page + 1 }} / {{ totalPages }}</span>
      <button @click="nextPage" :disabled="page + 1 >= totalPages">Next</button>
    </div>

    <div style="margin-top: 1rem;">
      <button @click="openCreateDialog">Create New Group</button>
    </div>

    <CreateEditDialog
        v-if="showDialog"
        :group="selectedGroup"
        @close="closeDialog"
        @saved="onSaved"
    />
  </div>
</template>
