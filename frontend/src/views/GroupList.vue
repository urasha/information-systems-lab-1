<script setup>
import {ref, onMounted} from 'vue';
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

async function fetchGroups() {
  const res = await api.get('/groups', {
    params: {
      page: page.value,
      size: pageSize,
      nameContains: filterName.value,
      sort: sortField.value,
      asc: sortAsc.value
    }
  });
  groups.value = res.data.content;
  console.log(groups.value);
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
  page.value++;
  fetchGroups();
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
  await api.delete(`/groups/${id}`);
  await fetchGroups();
}

onMounted(() => {
  fetchGroups();
  createWebSocket(() => fetchGroups());
});
</script>

<template>
  <div>
    <h1>Study Groups</h1>
    <input v-model="filterName" placeholder="Filter by name..." @input="fetchGroups"/>

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

    <div>
      <button @click="prevPage" :disabled="page===0">Prev</button>
      <span>Page {{ page + 1 }}</span>
      <button @click="nextPage" :disabled="groups.length < pageSize">Next</button>
    </div>

    <button @click="openCreateDialog">Create New Group</button>

    <CreateEditDialog
        v-if="showDialog"
        :group="selectedGroup"
        @close="closeDialog"
        @saved="fetchGroups"
    />
  </div>
</template>
