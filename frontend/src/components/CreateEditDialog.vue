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
    alert('Ошибка при сохранении группы: ' + err.response?.data?.message || err.message);
  }
}
</script>

<template>
  <div class="modal">
    <h2>{{ localGroup.id ? 'Edit' : 'Create' }} Group</h2>

    <input v-model="localGroup.name" placeholder="Group Name"/>
    <input type="number" v-model.number="localGroup.studentsCount" placeholder="Students Count"/>
    <input type="number" v-model.number="localGroup.expelledStudents" placeholder="Expelled Students"/>
    <input type="number" v-model.number="localGroup.transferredStudents" placeholder="Transferred Students"/>
    <input type="number" v-model.number="localGroup.shouldBeExpelled" placeholder="Should Be Expelled"/>
    <input type="number" step="0.1" v-model.number="localGroup.averageMark" placeholder="Average Mark"/>

    <select v-model="localGroup.semesterEnum">
      <option value="SECOND">SECOND</option>
      <option value="SIXTH">SIXTH</option>
      <option value="SEVENTH">SEVENTH</option>
      <option value="EIGHTH">EIGHTH</option>
    </select>

    <select v-model="localGroup.formOfEducation">
      <option :value="null">—</option>
      <option value="DISTANCE_EDUCATION">DISTANCE_EDUCATION</option>
      <option value="FULL_TIME_EDUCATION">FULL_TIME_EDUCATION</option>
      <option value="EVENING_CLASSES">EVENING_CLASSES</option>
    </select>

    <h3>Coordinates</h3>
    <input type="number" step="0.1" v-model.number="localGroup.coordinates.x" placeholder="X"/>
    <input type="number" v-model.number="localGroup.coordinates.y" placeholder="Y"/>

    <h3>Admin</h3>
    <input v-model="localGroup.groupAdmin.name" placeholder="Admin Name"/>
    <select v-model="localGroup.groupAdmin.eyeColor">
      <option value="BLACK">BLACK</option>
      <option value="BLUE">BLUE</option>
      <option value="WHITE">WHITE</option>
      <option value="BROWN">BROWN</option>
    </select>
    <select v-model="localGroup.groupAdmin.hairColor">
      <option value="BLACK">BLACK</option>
      <option value="BLUE">BLUE</option>
      <option value="WHITE">WHITE</option>
      <option value="BROWN">BROWN</option>
    </select>

    <h4>Admin Location</h4>
    <input type="number" step="0.1" v-model.number="localGroup.groupAdmin.location.x" placeholder="X"/>
    <input type="number" v-model.number="localGroup.groupAdmin.location.y" placeholder="Y"/>
    <input type="number" step="0.1" v-model.number="localGroup.groupAdmin.location.z" placeholder="Z"/>

    <button @click="save">Save</button>
    <button @click="$emit('close')">Cancel</button>
  </div>
</template>
