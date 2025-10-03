<script setup>
import {ref, watch, onMounted, computed} from 'vue';
import {api} from '../services/api';

const props = defineProps({group: Object});
const emit = defineEmits(['close', 'saved']);

const localGroup = ref({
  name: '',
  studentsCount: 1,
  expelledStudents: 1,
  transferredStudents: 1,
  shouldBeExpelled: 1,
  averageMark: 1,
  semesterEnum: 'SECOND',
  formOfEducation: null,
  coordinates: {x: 0, y: 0},
  groupAdmin: {
    name: '',
    eyeColor: 'BLACK',
    hairColor: 'BLACK',
    location: {x: 0, y: 0, z: 0}
  }
});

const fieldErrors = ref({});

const existingCoordinates = ref([]);
const existingAdmins = ref([]);
const selectedCoordinatesIndex = ref(-1);
const selectedAdminIndex = ref(-1);

const uniqueCoordinates = computed(() => {
  const seen = new Set();
  return existingCoordinates.value.filter(c => {
    const key = `${c.x}_${c.y}`;
    if (seen.has(key)) return false;
    seen.add(key);
    return true;
  });
});

const uniqueAdmins = computed(() => {
  const seen = new Set();
  return existingAdmins.value.filter(a => {
    const loc = a.location || {};
    const key = `${a.name}_${a.eyeColor}_${a.hairColor}_${loc.x}_${loc.y}_${loc.z}`;
    if (seen.has(key)) return false;
    seen.add(key);
    return true;
  });
});

watch(() => props.group, g => {
  if (g) localGroup.value = {...localGroup.value, ...g};
}, {immediate: true});

onMounted(async () => {
  try {
    const coordsRes = await api.get('/coordinates');
    existingCoordinates.value = coordsRes.data || [];

    const adminsRes = await api.get('/persons');
    existingAdmins.value = adminsRes.data || [];
  } catch (err) {
    console.error('Failed to load existing objects');
  }
});

async function save() {
  try {
    fieldErrors.value = {};

    if (selectedCoordinatesIndex.value >= 0) {
      const coord = existingCoordinates.value[selectedCoordinatesIndex.value];
      localGroup.value.coordinates = {x: coord.x, y: coord.y};
    }

    if (selectedAdminIndex.value >= 0) {
      const admin = existingAdmins.value[selectedAdminIndex.value];
      localGroup.value.groupAdmin = {
        name: admin.name,
        eyeColor: admin.eyeColor,
        hairColor: admin.hairColor,
        location: { x: admin.location.x, y: admin.location.y, z: admin.location.z }
      };
    }

    if (localGroup.value.id) {
      await api.put(`/groups/${localGroup.value.id}`, localGroup.value);
    } else {
      await api.post(`/groups`, localGroup.value);
    }
    emit('saved');
    emit('close');
  } catch (err) {
    console.error(err);
    if (err.response?.status === 400 && err.response?.data?.fieldErrors) {
      fieldErrors.value = err.response.data.fieldErrors;
    } else {
      alert('Group save error: ' + (err.response?.data?.message || err.message));
    }
  }
}
</script>

<template>
  <div class="modal-backdrop" @click.self="$emit('close')">
    <div class="modal">
      <h2>{{ localGroup.id ? 'Edit' : 'Create' }} Group</h2>

      <div class="form-section">
        <label>Group Name:</label>
        <input v-model="localGroup.name" placeholder="Group Name"/>
        <span class="error" v-if="fieldErrors.name">{{ fieldErrors.name }}</span>

        <label>Students Count:</label>
        <input type="number" v-model.number="localGroup.studentsCount" placeholder="Students Count"/>
        <span class="error" v-if="fieldErrors.studentsCount">{{ fieldErrors.studentsCount }}</span>

        <label>Expelled Students:</label>
        <input type="number" v-model.number="localGroup.expelledStudents" placeholder="Expelled Students"/>
        <span class="error" v-if="fieldErrors.expelledStudents">{{ fieldErrors.expelledStudents }}</span>

        <label>Transferred Students:</label>
        <input type="number" v-model.number="localGroup.transferredStudents" placeholder="Transferred Students"/>
        <span class="error" v-if="fieldErrors.transferredStudents">{{ fieldErrors.transferredStudents }}</span>

        <label>Should Be Expelled:</label>
        <input type="number" v-model.number="localGroup.shouldBeExpelled" placeholder="Should Be Expelled"/>
        <span class="error" v-if="fieldErrors.shouldBeExpelled">{{ fieldErrors.shouldBeExpelled }}</span>

        <label>Average Mark:</label>
        <input type="number" step="0.1" v-model.number="localGroup.averageMark" placeholder="Average Mark"/>
        <span class="error" v-if="fieldErrors.averageMark">{{ fieldErrors.averageMark }}</span>
      </div>

      <div class="form-section">
        <label>Semester:</label>
        <select v-model="localGroup.semesterEnum">
          <option value="SECOND">SECOND</option>
          <option value="SIXTH">SIXTH</option>
          <option value="SEVENTH">SEVENTH</option>
          <option value="EIGHTH">EIGHTH</option>
        </select>

        <label>Form of Education:</label>
        <select v-model="localGroup.formOfEducation">
          <option :value="null">—</option>
          <option value="DISTANCE_EDUCATION">DISTANCE_EDUCATION</option>
          <option value="FULL_TIME_EDUCATION">FULL_TIME_EDUCATION</option>
          <option value="EVENING_CLASSES">EVENING_CLASSES</option>
        </select>
      </div>

      <div class="form-section">
        <h3 class="section-title">Coordinates</h3>
        <label>Select existing:</label>
        <select v-model="selectedCoordinatesIndex">
          <option :value="-1">Create new</option>
          <option v-for="(coord, index) in uniqueCoordinates" :key="index" :value="index">
            X: {{coord.x}}, Y: {{coord.y}}
          </option>
        </select>

        <label>X:</label>
        <input type="number" step="0.1" v-model.number="localGroup.coordinates.x" :disabled="selectedCoordinatesIndex >= 0"/>
        <label>Y:</label>
        <input type="number" v-model.number="localGroup.coordinates.y" :disabled="selectedCoordinatesIndex >= 0"/>
      </div>

      <div class="form-section">
        <h3 class="section-title">Admin</h3>
        <label>Select existing:</label>
        <select v-model="selectedAdminIndex">
          <option :value="-1">Create new</option>
          <option v-for="(admin, index) in uniqueAdmins" :key="index" :value="index">
            {{admin.name}} ({{admin.eyeColor}}, {{admin.hairColor}})
          </option>
        </select>

        <label>Admin Name:</label>
        <input v-model="localGroup.groupAdmin.name" :disabled="selectedAdminIndex >= 0"/>
        <span class="error" v-if="fieldErrors['groupAdmin.name']">{{ fieldErrors['groupAdmin.name'] }}</span>

        <label>Eye Color:</label>
        <select v-model="localGroup.groupAdmin.eyeColor" :disabled="selectedAdminIndex >= 0">
          <option value="BLACK">BLACK</option>
          <option value="BLUE">BLUE</option>
          <option value="WHITE">WHITE</option>
          <option value="BROWN">BROWN</option>
        </select>

        <label>Hair Color:</label>
        <select v-model="localGroup.groupAdmin.hairColor" :disabled="selectedAdminIndex >= 0">
          <option value="BLACK">BLACK</option>
          <option value="BLUE">BLUE</option>
          <option value="WHITE">WHITE</option>
          <option value="BROWN">BROWN</option>
        </select>

        <h4 class="section-title">Admin Location</h4>
        <label>X:</label>
        <input type="number" step="0.1" v-model.number="localGroup.groupAdmin.location.x" :disabled="selectedAdminIndex >= 0"/>
        <label>Y:</label>
        <input type="number" v-model.number="localGroup.groupAdmin.location.y" :disabled="selectedAdminIndex >= 0"/>
        <label>Z:</label>
        <input type="number" step="0.1" v-model.number="localGroup.groupAdmin.location.z" :disabled="selectedAdminIndex >= 0"/>
      </div>

      <div class="modal-actions">
        <button class="save-btn" @click="save">Save</button>
        <button class="cancel-btn" @click="$emit('close')">Cancel</button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.modal-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 50;
}

.modal {
  background: #fff;
  border-radius: 8px;
  padding: 1.5rem;
  width: 500px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
}

.form-section {
  margin-bottom: 1rem;
  display: grid;
  grid-template-columns: 180px 1fr;
  row-gap: 0.5rem;
  column-gap: 0.5rem;
  align-items: center;
}

.form-section .section-title {
  grid-column: 1 / -1;
  margin: 0.5rem 0;
}

.form-section label {
  margin-left: 5%;
  font-weight: 500;
}

.form-section input,
.form-section select {
  width: 100%;
  padding: 0.3rem;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 0.5rem;
}

button {
  padding: 0.5rem 1rem;
  border-radius: 4px;
  border: none;
  cursor: pointer;
}

.save-btn {
  background: #4CAF50;
  color: white;
}

.save-btn:hover {
  background: #45a049;
}

.cancel-btn {
  background: #f44336;
  color: white;
}

.cancel-btn:hover {
  background: #d32f2f;
}

.error {
  color: red;
  font-size: 0.8rem;
  grid-column: 2 / 3;
}
</style>


