<script setup>
const props = defineProps({
  group: { type: Object, required: true }
});
const emit = defineEmits(['close']);
</script>

<template>
  <div class="modal-backdrop" @click.self="$emit('close')">
    <div class="modal">
      <h2>Group #{{ group.id }} — {{ group.name }}</h2>

      <section class="info-block">
        <h3>Basic Info</h3>
        <dl>
          <dt>ID:</dt><dd>{{ group.id }}</dd>
          <dt>Name:</dt><dd>{{ group.name }}</dd>
          <dt>Form of Education:</dt><dd>{{ group.formOfEducation ?? '—' }}</dd>
          <dt>Semester:</dt><dd>{{ group.semesterEnum }}</dd>
        </dl>
      </section>

      <section class="info-block">
        <h3>Academic Data</h3>
        <dl>
          <dt>Students:</dt><dd>{{ group.studentsCount }}</dd>
          <dt>Expelled:</dt><dd>{{ group.expelledStudents }}</dd>
          <dt>Transferred:</dt><dd>{{ group.transferredStudents }}</dd>
          <dt>Debtors:</dt><dd>{{ group.shouldBeExpelled }}</dd>
          <dt>Average Mark:</dt><dd>{{ group.averageMark }}</dd>
        </dl>
      </section>

      <section class="info-block">
        <h3>Coordinates</h3>
        <dl>
          <dt>X:</dt><dd>{{ group.coordinates?.x }}</dd>
          <dt>Y:</dt><dd>{{ group.coordinates?.y }}</dd>
        </dl>
      </section>

      <section class="info-block">
        <h3>Administrator</h3>
        <dl>
          <dt>Name:</dt><dd>{{ group.groupAdmin?.name || '—' }}</dd>
          <dt>Eye Color:</dt><dd>{{ group.groupAdmin?.eyeColor }}</dd>
          <dt>Hair Color:</dt><dd>{{ group.groupAdmin?.hairColor }}</dd>
        </dl>

        <h4>Location</h4>
        <dl>
          <dt>X:</dt><dd>{{ group.groupAdmin?.location?.x }}</dd>
          <dt>Y:</dt><dd>{{ group.groupAdmin?.location?.y }}</dd>
          <dt>Z:</dt><dd>{{ group.groupAdmin?.location?.z }}</dd>
        </dl>
      </section>

      <div class="modal-actions">
        <button class="close-btn" @click="$emit('close')">Close</button>
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
  width: 600px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
}

h2 {
  margin-bottom: 1rem;
}

.info-block {
  margin-bottom: 1rem;
  padding: 1rem;
  border: 1px solid #ddd;
  border-radius: 6px;
  background: #fafafa;
}

dl {
  display: grid;
  grid-template-columns: 150px 1fr;
  gap: 0.5rem 1rem;
}

dt {
  font-weight: bold;
  text-align: right;
}

dd {
  margin: 0;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 1rem;
}

.close-btn {
  padding: 0.5rem 1rem;
  border-radius: 4px;
  border: none;
  cursor: pointer;
  background: #f44336;
  color: white;
}

.close-btn:hover {
  background: #d32f2f;
}
</style>
