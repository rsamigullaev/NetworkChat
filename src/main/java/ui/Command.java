package ui;

import lombok.AllArgsConstructor;
import lombok.Getter;

// TODO
@AllArgsConstructor
@Getter
public enum Command {
    Exit("/exit"),
    Img("/img");

    private final String value;
}
