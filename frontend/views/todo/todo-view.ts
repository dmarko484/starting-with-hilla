import { html } from 'lit';

import { customElement, state } from 'lit/decorators.js';

import '@vaadin/vaadin-text-field';
import '@vaadin/vaadin-button';
import '@vaadin/vaadin-checkbox';
import { Binder, field } from '@hilla/form';
import TodoModel from 'Frontend/generated/com/example/application/TodoModel';
import Todo from 'Frontend/generated/com/example/application/Todo';
import { TodoEndpoint } from 'Frontend/generated/endpoints';
import { View } from '../view';



@customElement('todo-view')
export class TodoView extends View {
	@state()
	private txt?: String = "my default";
	private binder = new Binder(this, TodoModel);
	
	@state()
  	private todos: Todo[] = [];
  	
  	@state()
  	private message: String = "-";
	
  render() {
	//this.text = await this.getText();
	this.loadDefaults();
    return html`
    <div style="padding: 10px; background-color: red; color: white">
      Message: ${this.message}
    </div>
    <div>
     New Task: 
    <vaadin-text-field ${field(this.binder.model.task)}></vaadin-text-field> - 
    <vaadin-text-field ${field(this.binder.model.author)}></vaadin-text-field>
    <vaadin-button  theme="primary" @click=${this.createTodo} ?disabled=${this.binder.invalid}> Add Todo </vaadin-button>
    <vaadin-button theme="primary" @click=${this.removeTodos} > Remove done items </vaadin-button>
    </div>
    <div class="todos">
        <table>
          <tr>
            <th>Status</th>
            <th style="width: 200px">Task</th>
            <th>Author</th>
           </tr>
           ${this.todos.map(
          (todo) => html`
            <tr class="todo">
             <td>
              <vaadin-checkbox
                id=${todo.id}
              
                ?checked=${todo.done}
                @checked-changed=${(e: CustomEvent) => this.updateTodoState(todo, e.detail.value)}
              ></vaadin-checkbox>
              </td>
              <td>${todo.task}</td>
              <td>${todo.author}</td>
            </tr>
          `
        )}
		</table>
</div>
<div style="margin-top: 20px">
	<a href="/excel" target="_blank"> Download as MS Excel </a>
</div>
    `;
  }

  async connectedCallback() {
    super.connectedCallback();
    this.classList.add(
      'flex',
      'flex-col',
      'h-full',
      'p-l',
      'box-border'
    );
    this.todos = await TodoEndpoint.findAll();
    
    TodoEndpoint.join().onNext((msg) => {
          this.message = msg;
          this.loadTodos();
     });
  }
  
  async createTodo() {
	const createdTodo = await this.binder.submitTo(TodoEndpoint.save);
	if (createdTodo) {
		//this.todos = await TodoEndpoint.findAll();
		TodoEndpoint.send("New todo created: " + createdTodo.task);
		this.binder.clear();
	}
}
  
  async loadDefaults() {
	this.txt = await TodoEndpoint.getText();
}

  updateTodoState(todo: Todo, done: boolean) {
    todo.done = done;
    const updatedTodo = { ...todo };
    this.todos = this.todos.map((t) => (t.id === todo.id ? updatedTodo : t));
    TodoEndpoint.save(updatedTodo);
  }
  
    async removeTodos() {
     this.todos = []; 
     await TodoEndpoint.removeDoneItems();
     TodoEndpoint.send("Items removed ...");
     
  }
  
  async loadTodos() {
    this.todos = await TodoEndpoint.findAll();
  }

}
