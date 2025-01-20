package ru.gpb.grules.ruleEngine;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import ru.gpb.grules.Console;
import ru.gpb.grules.businessModel.CreditProgram;
import ru.gpb.grules.exception.ParseException;
import ru.gpb.grules.parserModel.ExcelParser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class RuleEngine {

    private static final Logger LOG = LogManager.getLogger();
    private static final String RULES_PATH = "Rules";
    private List<BaseRule> rules;

    @EventListener(ApplicationReadyEvent.class)
    public void readDir() throws IOException, NoSuchFieldException, ClassNotFoundException, ParseException {
        rules = new ArrayList<>();
        String basePath = Console.class.getClassLoader().getResource(RULES_PATH).getPath();
        File baseDir = new File(basePath);
        readRecursion(baseDir);
        Collections.sort(rules);
    }

    private void readRecursion(File dir) throws IOException, ParseException, NoSuchFieldException, ClassNotFoundException {
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                readRecursion(file);
            } else if (file.getName().endsWith(".xlsx")) {
                ExcelParser parser = new ExcelParser(file);
                rules.addAll(parser.readFile());
            }
        }
    }

    public void perform(Object ...args) {
        CreditProgram creditProgram = new CreditProgram();
        LOG.info("RULES STARTED");
        for (Performable performable : rules) {
            performable.perform(args);
        }
        LOG.info("RULES FINISHED");
    }
}
