# NanoKafka

A toy Kafka that supports s3 as storage. 

## Phase 1: Working example

In memory kafka
- logSegment
- record
- topic
- no partition
### Spec
- when single producer send record, single consumer can consume

## Phase 2: On disk

Save log segment to disk
- index file
- time index file (optional)
### Spec
- when single producer send record, single consumer can consume
- When broker restart, all data must persist, and when new consumer consume records, 
  - it should get data

