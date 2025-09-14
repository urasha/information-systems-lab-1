<template>
  <div id="app">
    <header>
      <h1>Study Groups Management</h1>
    </header>

    <main>
      <router-view />
    </main>
  </div>
</template>

<style scoped>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  margin: 0;
  padding: 0 2rem;
}

header {
  background-color: #42b983;
  color: white;
  padding: 1rem;
  margin-bottom: 1rem;
}

main {
  min-height: 80vh;
}
</style>
