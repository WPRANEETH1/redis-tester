package com.missakai.redistester.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@NoArgsConstructor
@Data
@ToString
public class KeyValuePairDto implements Serializable {

    private String key;
    private JsonNode value;
}
