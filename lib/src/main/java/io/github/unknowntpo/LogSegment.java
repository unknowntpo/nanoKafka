package io.github.unknowntpo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LogSegment {

    private final String name;
    private final List<Record> records;

    public LogSegment(String name, List<Record> records) {
        this.name = name;
        this.records = records;
    }

    public void appendRecord(Record record) {
        records.add(record);
    }

    public int recordSize() {
        return records.size();
    }

    public Record getRecord(int offset) {
        return  records.get(offset);
    }
}
