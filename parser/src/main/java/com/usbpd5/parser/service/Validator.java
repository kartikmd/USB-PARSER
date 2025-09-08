package com.usbpd5.parser.service;


import com.usbpd5.parser.model.Section;
import com.usbpd5.parser.model.ValidationResult;

import java.util.List;

public interface Validator {
    ValidationResult validate(List<Section> tocSections, List<Section> parsedSections);
}
