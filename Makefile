SHELL := bash
GRADLEW := ./gradlew

PACK_INSTANCE := quantum-skies-0.8.1
MOD_VERSION = 0.1.1

PRISMLAUNCHER_INSTANCE := PrismLauncher/instances/${PACK_INSTANCE}/minecraft/

LINUX_INSTALL := ${HOME}/.local/share/${PRISMLAUNCHER_INSTANCE}
MAC_INSTALL := ${HOME}/Library/Application Support/${PRISMLAUNCHER_INSTANCE}
WIN_INSTALL := $(shell ls -1d /mnt/c/Users/*/AppData/Roaming/${PRISMLAUNCHER_INSTANCE} 2>/dev/null | head -n 1)

# INSTALLPATH resolves to the first detected PrismLauncher instance location across Linux, macOS, and WSL.
INSTALLPATH ?=
ifeq ($(strip $(INSTALLPATH)),)
ifneq ($(shell test -d "$(LINUX_INSTALL)" && printf 'y'),)
INSTALLPATH := ${LINUX_INSTALL}
else ifneq ($(shell test -d "$(MAC_INSTALL)" && printf 'y'),)
INSTALLPATH := ${MAC_INSTALL}
else ifneq ($(strip $(WIN_INSTALL)),)
INSTALLPATH := ${WIN_INSTALL}
else
INSTALLPATH := ${LINUX_INSTALL}
$(warning INSTALLPATH not detected; defaulting to ${INSTALLPATH})
endif
endif

MODS_DIR ?= ${INSTALLPATH}/mods

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

install: build
	cp -v build/libs/gcyrextras-1.20.1-0.1.1.jar "$(MODS_DIR)/"
