<script setup>
import {computed, ref} from 'vue';
import {api} from '../services/api';

const props = defineProps({
  groups: {type: Array, default: () => []}
});
const emit = defineEmits(['close', 'done']);

const loading = ref(false);
const error = ref(null);
const success = ref(null);

const adminName = ref('');
const searchSubstring = ref('');
const uniqueAdmins = ref([]);
const searchResults = ref([]);

const expelGroupId = ref(null);
const transferFrom = ref(null);
const transferTo = ref(null);

function clearMessages() {
  error.value = null;
  success.value = null;
}

async function deleteByAdmin() {
  if (!adminName.value.trim()) {
    error.value = 'Please provide an admin name';
    return;
  }
  if (!confirm(`Delete ALL groups with admin = "${adminName.value}" ? This cannot be undone.`)) return;

  loading.value = true;
  clearMessages();
  try {
    await api.delete('/groups/special/by-admin', {params: {adminName: adminName.value}});
    success.value = `Groups with admin "${adminName.value}" deleted.`;
    emit('done');
  } catch (e) {
    error.value = (e.response?.data?.message) || (e.message || 'Server error');
  } finally {
    loading.value = false;
  }
}

async function doSearchByName() {
  clearMessages();
  if (!searchSubstring.value.trim()) {
    error.value = 'Please enter substring to search';
    return;
  }
  loading.value = true;
  try {
    const res = await api.get('/groups/special/search', {params: {substring: searchSubstring.value}});
    searchResults.value = res.data || [];
    if (!Array.isArray(searchResults.value)) searchResults.value = [];
    success.value = `Found ${searchResults.value.length} result(s).`;
  } catch (e) {
    error.value = e.response?.data?.message || e.message || 'Server error';
  } finally {
    loading.value = false;
  }
}

async function loadUniqueAdmins() {
  clearMessages();
  loading.value = true;
  try {
    const res = await api.get('/groups/special/unique-admins');
    uniqueAdmins.value = res.data || [];
    success.value = `Loaded ${uniqueAdmins.value.length} unique admin(s).`;
  } catch (e) {
    error.value = e.response?.data?.message || e.message || 'Server error';
  } finally {
    loading.value = false;
  }
}

async function expelGroup() {
  clearMessages();
  if (!expelGroupId.value) {
    error.value = 'Choose a group to expel';
    return;
  }
  if (!confirm(`Expel all students in group id=${expelGroupId.value}?`)) return;
  loading.value = true;
  try {
    await api.post(`/groups/special/${expelGroupId.value}/expel`);
    success.value = `All students expelled from group ${expelGroupId.value}.`;
    emit('done');
  } catch (e) {
    error.value = e.response?.data?.message || e.message || 'Server error';
  } finally {
    loading.value = false;
  }
}

async function transferStudents() {
  clearMessages();
  if (!transferFrom.value || !transferTo.value) {
    error.value = 'Pick both source and target groups';
    return;
  }
  if (transferFrom.value === transferTo.value) {
    error.value = 'Source and target must be different';
    return;
  }
  if (!confirm(`Transfer all students from ${transferFrom.value} → ${transferTo.value}?`)) return;
  loading.value = true;
  try {
    await api.post('/groups/special/transfer', null, {params: {fromId: transferFrom.value, toId: transferTo.value}});
    success.value = `Students transferred from ${transferFrom.value} to ${transferTo.value}.`;
    emit('done');
  } catch (e) {
    error.value = e.response?.data?.message || e.message || 'Server error';
  } finally {
    loading.value = false;
  }
}

const groupOptions = computed(() => (props.groups || []).map(g => ({label: `#${g.id} — ${g.name}`, value: g.id})));
</script>

<template>
  <div class="modal-backdrop" @click.self="emit('close')">
    <div class="modal big">
      <div class="modal-header">
        <h2>Special Operations</h2>
        <button class="close-x" @click="$emit('close')">✕</button>
      </div>

      <div class="ops-grid">
        <div class="card">
          <h3>Delete groups by admin</h3>
          <p class="muted">Delete all StudyGroup objects whose <code>groupAdmin</code> equals given value.</p>
          <input v-model="adminName" placeholder="Admin name"/>
          <div class="row">
            <button @click="deleteByAdmin" :disabled="loading">Delete</button>
          </div>
        </div>

        <div class="card">
          <h3>Search by name</h3>
          <p class="muted">Return groups whose <code>name</code> contains given substring (case-insensitive).</p>
          <input v-model="searchSubstring" placeholder="Substring"/>
          <div class="row">
            <button @click="doSearchByName" :disabled="loading">Search</button>
          </div>

          <div v-if="searchResults.length" class="results">
            <h4>Results ({{ searchResults.length }})</h4>
            <table class="mini-table">
              <thead>
              <tr>
                <th>ID</th>
                <th>Name</th>
                <th>Students</th>
                <th>Admin</th>
              </tr>
              </thead>
              <tbody>
              <tr v-for="g in searchResults" :key="g.id">
                <td>{{ g.id }}</td>
                <td>{{ g.name }}</td>
                <td>{{ g.studentsCount }}</td>
                <td>{{ g.groupAdmin?.name || '—' }}</td>
              </tr>
              </tbody>
            </table>
          </div>
        </div>

        <div class="card">
          <h3>Unique admins</h3>
          <p class="muted">Get array of unique <code>groupAdmin</code> values.</p>
          <div class="row">
            <button @click="loadUniqueAdmins" :disabled="loading">Load Unique Admins</button>
          </div>
          <ul v-if="uniqueAdmins.length" class="simple-list">
            <li v-for="a in uniqueAdmins" :key="a">{{ a }}</li>
          </ul>
        </div>

        <div class="card">
          <h3>Expel all students in group</h3>
          <p class="muted">Set <code>expelledStudents</code> according to server logic.</p>
          <select v-model.number="expelGroupId">
            <option :value="null">— select group —</option>
            <option v-for="opt in groupOptions" :key="opt.value" :value="opt.value">{{ opt.label }}</option>
          </select>
          <div class="row">
            <button @click="expelGroup" :disabled="loading || !expelGroupId">Expel</button>
          </div>
        </div>

        <div class="card">
          <h3>Transfer all students</h3>
          <p class="muted">Move all students from one group to another.</p>
          <div class="two-selects">
            <select v-model.number="transferFrom">
              <option :value="null">From —</option>
              <option v-for="opt in groupOptions" :key="opt.value" :value="opt.value">{{ opt.label }}</option>
            </select>
            <select v-model.number="transferTo">
              <option :value="null">To —</option>
              <option v-for="opt in groupOptions" :key="opt.value" :value="opt.value">{{ opt.label }}</option>
            </select>
          </div>
          <div class="row">
            <button @click="transferStudents" :disabled="loading || !transferFrom || !transferTo">Transfer</button>
          </div>
        </div>
      </div>

      <div class="modal-footer">
        <div class="messages">
          <div v-if="error" class="err">{{ error }}</div>
          <div v-if="success" class="ok">{{ success }}</div>
        </div>
        <div class="actions">
          <button @click="$emit('close')">Close</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.modal-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1200;
}

.modal {
  background: #fff;
  border-radius: 8px;
  width: 920px;
  max-height: 90vh;
  overflow: auto;
  padding: 16px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.25);
}

.modal.big {
  width: 920px;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.close-x {
  background: none;
  border: none;
  font-size: 18px;
  cursor: pointer;
}

.ops-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.card {
  border: 1px solid #e6e6e6;
  padding: 12px;
  border-radius: 6px;
  background: #fafafa;
}

.card h3 {
  margin: 0 0 6px 0;
  font-size: 1.05rem;
}

.card .muted {
  color: #666;
  font-size: 0.85rem;
  margin-bottom: 8px;
}

.card input, .card select {
  width: 100%;
  padding: 8px;
  margin-bottom: 8px;
  border-radius: 4px;
  border: 1px solid #ccc;
  box-sizing: border-box;
}

.card .row {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}

.two-selects {
  display: flex;
  gap: 8px;
}

.mini-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 8px;
}

.mini-table th, .mini-table td {
  border: 1px solid #ddd;
  padding: 6px;
  text-align: left;
}

.simple-list {
  list-style: none;
  padding-left: 0;
  margin: 8px 0 0 0;
  max-height: 120px;
  overflow: auto;
  border: 1px solid #eee;
  border-radius: 4px;
  padding: 6px;
  background: #fff;
}

.simple-list li {
  padding: 4px 6px;
  border-bottom: 1px dashed #f1f1f1;
}

.modal-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
}

.err {
  color: #b00020;
}

.ok {
  color: #166534;
}

button {
  padding: 8px 12px;
  border-radius: 6px;
  border: none;
  cursor: pointer;
  background: #1976d2;
  color: white;
}

button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>
