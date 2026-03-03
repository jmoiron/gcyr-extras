SHELL := bash
GRADLEW := ./gradlew

.PHONY: all build data clean

# Default: build the mod
all: build

build:
	$(GRADLEW) build

# Run data generation (writes to src/generated/resources)
data:
	$(GRADLEW) runData

# Clean build artifacts and generated outputs
clean:
	$(GRADLEW) clean
	rm -rf build/ src/generated/resources/ runs/

