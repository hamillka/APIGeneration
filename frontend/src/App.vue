<template>
  <div id="app">
    <div class="large-12 medium-12 small-12 cell" style="margin: 25px">
      <label>Прикрепить файл &nbsp;&nbsp;&nbsp;&nbsp;
        <input type="file" id="fileSelector" ref="file" @change="handleFileUpload()"/>
      </label>
      <br/>
      <br/>
      <label for="integerInput">Количество:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</label>
      <input type="number" id="integerInput" v-model="amount"/>
      <br/>
      <br/>
      <label for="integerInput">Seed:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
      </label>
      <input type="text" id="stringInput" v-model="seed"/>
      <br/>
      <br/>
      <label>
        <button @click="executeCommand()">Запустить</button>
      </label>
    </div>
  </div>
</template>

<script>

export default {
  data() {
    return {
      file: '',
      amount: 0,
      seed: "",
    }
  },

  methods: {
    handleFileUpload() {
      this.file = this.$refs.file.files[0];
    },
    executeCommand() {
      if ((this.file !== '') && (this.amount === 0) && (this.seed === "")) {
        const socket = new WebSocket("ws://localhost:3000/oneparam");
        socket.onopen = () => {
          socket.send(this.file["name"])
        }
      }
      else if ((this.file !== '') && (this.amount !== 0) && (this.seed === "")) {
        const socket = new WebSocket("ws://localhost:3000/twoparams");
        socket.onopen = () => {
          socket.send(this.file["name"] + ' ' + this.amount)
        }
      }
      else if ((this.file !== '') && (this.amount !== 0) && (this.seed !== "")) {
        const socket = new WebSocket("ws://localhost:3000/threeparams");
        socket.onopen = () => {
          socket.send(this.file["name"] + ' ' + this.amount + ' ' + this.seed)
        }
      }
      else {
        alert("ERROR");
      }
    }
  }
}
</script>
