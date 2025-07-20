package com.si516.saludconecta.event;

import org.springframework.context.ApplicationEvent;

public class TranscriptionCompletedEvent extends ApplicationEvent {
    private final String audioId;

    public TranscriptionCompletedEvent(Object source, String audioId) {
        super(source);
        this.audioId = audioId;
    }

    public String getAudioId() {
        return audioId;
    }
}