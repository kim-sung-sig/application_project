<template>
  <div class="input-component">
    <div class="input-container">
      <input
        class="uk-input"
        v-model="inputValue"
        :id="id"
        :type="type"
        :placeholder="placeholder"
        :disabled="disabled"
      />
      <label :for="id">{{ text }}</label>
      <span v-show="errorMessage" class="error-message">{{ errorMessage }}</span>
    </div>
  </div>
</template>

<script>
export default {
  name: "TextInput",
  props: {
    value: {
      type: [String, Number],
      default: "",
    },
    id: {
      type: String,
      required: true
    },
    type: {
      type: String,
      default: "text"
    },
    placeholder: {
      type: String,
      default: ""
    },
    disabled: {
      type: Boolean,
      default: false
    },
    text: {
      type: String,
      required: true,
    },
    errorMessage: {
      type: String,
      default: "",
    },
  },
  data() {
    return {
      inputValue: this.value,
    };
  },
  mounted() {
    this.inputValue = this.value;
  },
  watch: {
    inputValue(newValue) {
      this.$emit("input", newValue);
    },
  }
};
</script>

<style scoped>
.input-component {
    margin-bottom: 28px;
}

.input-container {
    position: relative;
    width: 100%;
}

.error-message {
    color: red;
    font-size: 12px;
    text-align: right;
    width: 100%;
    display: block;
    position: absolute;
    top : -17px;
    right: 0;
}

.input-container input {
    width: 100%;
    padding: 10px;
    font-size: 16px;
    border: 1px solid #ccc;
    border-radius: 5px;
    outline: none;
    box-sizing: border-box;
}

/* 라벨 스타일 */
.input-container label {
    position: absolute;
    top: 50%;
    left: 10px;
    transform: translateY(-50%);
    font-size: 16px;
    color: #aaa;
    pointer-events: none;
    transition: all 0.2s ease-in-out;
}

.input-container input:focus + label,
.input-container input:not(:placeholder-shown) + label,
.input-container input[value] + label {
    top: -10px;
    left: 10px;
    font-size: 12px;
    color: #007bff;
}
</style>
