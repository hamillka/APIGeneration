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
      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
      <input type="checkbox" v-model="checkBox" true-value="true" false-value="false"/>
      <label for="nojson">&nbsp;Только случайные объекты</label>
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
      checkBox: "false",
      seed: "",
    }
  },

  methods: {
    handleFileUpload() {
      this.file = this.$refs.file.files[0];
    },
    executeCommand() {
      let params = this.file["name"];
      let socket = null;
      console.log(this.checkBox)

      if ((this.file !== '') && (this.amount === 0) && (this.seed === "")) {
        socket = new WebSocket("ws://localhost:3030/oneparam");
      }
      else if ((this.file !== '') && (this.amount !== 0) && (this.seed === "")) {
        socket = new WebSocket("ws://localhost:3030/twoparams");
        params += ` ${this.amount}`
        if (this.checkBox === "true") {
          params += "@"
        }
      }
      else if ((this.file !== '') && (this.amount !== 0) && (this.seed !== "")) {
        socket = new WebSocket("ws://localhost:3030/threeparams");
        params += ` ${this.amount} ${this.seed}`
        if (this.checkBox === "true") {
          params += "@"
        }
      }
      else {
        alert("ERROR. Incorrect arguments");
      }
      if (socket !== null) {
        socket.onopen = () => {
          socket.send(params)
        }
      }
      else {
        alert("ERROR. No WebSocket")
      }
    }
  }
}
</script>
