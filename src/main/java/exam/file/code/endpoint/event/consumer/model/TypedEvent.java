package exam.file.code.endpoint.event.consumer.model;

import exam.file.code.PojaGenerated;
import exam.file.code.endpoint.event.model.PojaEvent;

@PojaGenerated
public record TypedEvent(String typeName, PojaEvent payload) {}
