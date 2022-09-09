package com.example.application;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.vaadin.flow.server.auth.AnonymousAllowed;

import dev.hilla.Endpoint;
import dev.hilla.Nonnull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Endpoint
@AnonymousAllowed
public class TodoEndpoint {

	private TodoRepository repository;

	private Sinks.Many<String> infoSink;

	public TodoEndpoint(TodoRepository repository) {
		super();
		this.repository = repository;
		infoSink = Sinks.many().multicast().directBestEffort();
	}

	public Todo save(Todo todo) {
		return repository.save(todo);
	}

	public @Nonnull List<@Nonnull Todo> findAll() {
		return StreamSupport.stream(repository.findAll().spliterator(), false).collect(Collectors.toList());
	}

	public String getText() {
		return "Todo custom title 2";
	}

	public @Nonnull List<@Nonnull Todo> removeDoneItems() {
		List<@Nonnull Todo> doneItems = findAll().stream().filter(t -> t.isDone()).collect(Collectors.toList());

		repository.deleteAll(doneItems);
		return findAll();
	}

	public void send(@Nonnull String message) {
		infoSink.emitNext(message, (signalType, emitResult) -> (emitResult == Sinks.EmitResult.FAIL_NON_SERIALIZED));
	}

	@Nonnull
	public Flux<@Nonnull String> join() {
		return infoSink.asFlux();
	}

}
