package com.si516.saludconecta.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class NewAudioStoredEvent extends ApplicationEvent {

    private final String fileId;
    private final String filename;

    public NewAudioStoredEvent(Object source, String fileId, String filename) {
        super(source);
        this.fileId = fileId;
        this.filename = filename;
    }
}
