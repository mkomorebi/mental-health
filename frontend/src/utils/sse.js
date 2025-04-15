class SSE {
    constructor(url, options = {}) {
      this.url = url;
      this.options = options;
      this.eventSource = null;
      this.listeners = new Map();
    }
  
    open() {
      if (!this.eventSource) {
        this.eventSource = new EventSource(this.url, this.options);
        this.eventSource.addEventListener('open', this.handleOpen.bind(this));
        this.eventSource.addEventListener('message', this.handleMessage.bind(this));
        this.eventSource.addEventListener('error', this.handleError.bind(this));
      }
    }
  
    close() {
      if (this.eventSource) {
        this.eventSource.close();
        this.eventSource = null;
        this.listeners.clear();
      }
    }
  
    on(eventName, callback) {
      if (!this.listeners.has(eventName)) {
        this.listeners.set(eventName, []);
      }
      this.listeners.get(eventName).push(callback);
    }
  
    off(eventName, callback) {
      if (this.listeners.has(eventName)) {
        const listeners = this.listeners.get(eventName);
        const index = listeners.indexOf(callback);
        if (index !== -1) {
          listeners.splice(index, 1);
        }
        if (listeners.length === 0) {
          this.listeners.delete(eventName);
        }
      }
    }
  
    handleOpen(event) {
      console.log('SSE connection opened');
    }
  
    handleMessage(event) {
      const data = JSON.parse(event.data);
      const eventName = data.event;
      if (this.listeners.has(eventName)) {
        const listeners = this.listeners.get(eventName);
        for (const listener of listeners) {
          listener(data);
        }
      }
    }
  
    handleError(event) {
      console.error('SSE connection error', event);
    }
  }
  
  export default SSE;

/**
 * 使用
    import SSE from './sse';

    const sse = new SSE('https://api.example.com/events');

    sse.open();

    sse.on('message', (data) => {
        console.log(data);
    });

    // 在需要关闭连接的时候调用
    sse.close();

*/

