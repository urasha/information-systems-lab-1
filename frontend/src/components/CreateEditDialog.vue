<script setup>
import {ref, watch} from 'vue';
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

watch(() => props.group, g => {
  if (g) localGroup.value = {...localGroup.value, ...g};
}, {immediate: true});

async function save() {
  try {
    if (localGroup.value.id) {
      await api.put(`/groups/${localGroup.value.id}`, localGroup.value);
    } else {
      await api.post(`/groups`, localGroup.value);
    }
    emit('saved');
    emit('close');
  } catch (err) {
    console.error(err);
    alert('Group creation error: ' + err.response?.data?.message || err.message);
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

        <label>Students Count:</label>
        <input type="number" v-model.number="localGroup.studentsCount" placeholder="Students Count"/>

        <label>Expelled Students:</label>
        <input type="number" v-model.number="localGroup.expelledStudents" placeholder="Expelled Students"/>

        <label>Transferred Students:</label>
        <input type="number" v-model.number="localGroup.transferredStudents" placeholder="Transferred Students"/>

        <label>Should Be Expelled:</label>
        <input type="number" v-model.number="localGroup.shouldBeExpelled" placeholder="Should Be Expelled"/>

        <label>Average Mark:</label>
        <input type="number" step="0.1" v-model.number="localGroup.averageMark" placeholder="Average Mark"/>
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
        <label>X:</label>
        <input type="number" step="0.1" v-model.number="localGroup.coordinates.x" placeholder="X"/>

        <label>Y:</label>
        <input type="number" v-model.number="localGroup.coordinates.y" placeholder="Y"/>
      </div>

      <div class="form-section">
        <h3 class="section-title">Admin</h3>

        <label>Admin Name:</label>
        <input v-model="localGroup.groupAdmin.name" placeholder="Admin Name"/>

        <label>Eye Color:</label>
        <select v-model="localGroup.groupAdmin.eyeColor">
          <option value="BLACK">BLACK</option>
          <option value="BLUE">BLUE</option>
          <option value="WHITE">WHITE</option>
          <option value="BROWN">BROWN</option>
        </select>

        <label>Hair Color:</label>
        <select v-model="localGroup.groupAdmin.hairColor">
          <option value="BLACK">BLACK</option>
          <option value="BLUE">BLUE</option>
          <option value="WHITE">WHITE</option>
          <option value="BROWN">BROWN</option>
        </select>

        <h4 class="section-title">Admin Location</h4>
        <label>X:</label>
        <input type="number" step="0.1" v-model.number="localGroup.groupAdmin.location.x" placeholder="X"/>

        <label>Y:</label>
        <input type="number" v-model.number="localGroup.groupAdmin.location.y" placeholder="Y"/>

        <label>Z:</label>
        <input type="number" step="0.1" v-model.number="localGroup.groupAdmin.location.z" placeholder="Z"/>
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
</style>


