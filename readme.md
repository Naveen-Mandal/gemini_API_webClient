# Gemini LLM Integration Engine

A reactive, non-blocking Spring Boot framework designed to integrate with Google's Gemini Pro API models for optimized content generation stream workflows.

## 🛠️ Core Engineering & System Resilience

This project shifts away from blocking I/O models to utilize reactive network data streams while enforcing strict enterprise fault-tolerance strategies.

### 1. Reactive Network Optimization
* **WebClient Engine:** Implemented Spring's asynchronous `WebClient` pipeline to execute non-blocking outbound HTTP requests, optimizing thread utilization over legacy blocking alternatives.
* **JSON Streaming:** Configured robust data mapping layer models to serialize request payloads and parse deeply nested downstream JSON structures safely.

### 2. Failure Prevention & System Traceability
* **Exponential Backoff:** Engineered dynamic resilience mechanisms via Reactor `Retry` specifications, safely managing remote gateway rate limits (`HTTP 429`) and standard network socket timeouts.
* **Diagnostic Trace Channels:** Configured low-level Netty network wire logs (`TRACE` and `DEBUG` layers) to dump exact packet allocation footprints into diagnostic tracking setups, streamlining root-cause isolation.
* **Structured Exception Mapping:** Wrapped data pipelines within functional exception handling boundaries (`onErrorResume`) to translate network layer drops into predictable, cleanly logged error states.

## 🚀 Environment Setup & Cross-Platform Execution

The codebase maintains full environment parity across local Windows sandboxes and Linux production micro-containers.

### Configuration Properties
Configure your target keys in `src/main/resources/application.properties`:
```properties
logging.level.com.naveenmandal.project1=DEBUG
logging.level.org.springframework.web.reactive.function.client.ExchangeFunctions=TRACE