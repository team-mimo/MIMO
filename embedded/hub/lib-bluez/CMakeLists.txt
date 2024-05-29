cmake_minimum_required(VERSION 3.10)

project(
		BluezDbusLib LANGUAGES C
	   )

find_package(PkgConfig REQUIRED)
pkg_check_modules(GLIB REQUIRED IMPORTED_TARGET glib-2.0)
pkg_check_modules(GIO_UNIX REQUIRED IMPORTED_TARGET gio-unix-2.0)

add_custom_command(OUTPUT ${CMAKE_CURRENT_BINARY_DIR}/org-bluez-adapter1.c
                   COMMAND gdbus-codegen --interface-prefix org.bluez.Adapter1. --generate-c-code ${CMAKE_CURRENT_BINARY_DIR}/org-bluez-adapter1 ${CMAKE_CURRENT_SOURCE_DIR}/org.bluez.Adapter1.xml
                   DEPENDS ${CMAKE_CURRENT_SOURCE_DIR}/org.bluez.Adapter1.xml
                   COMMENT "Generate D-Bus 'org.bluez.Adapter1.xml'"
                   )

add_custom_command(OUTPUT ${CMAKE_CURRENT_BINARY_DIR}/org-bluez-battery1.c
                   COMMAND gdbus-codegen --interface-prefix org.bluez.Battery1. --generate-c-code ${CMAKE_CURRENT_BINARY_DIR}/org-bluez-battery1 ${CMAKE_CURRENT_SOURCE_DIR}/org.bluez.Battery1.xml
                   DEPENDS ${CMAKE_CURRENT_SOURCE_DIR}/org.bluez.Battery1.xml
                   COMMENT "Generate D-Bus 'org.bluez.Battery1.xml'"
                   )

add_custom_command(OUTPUT ${CMAKE_CURRENT_BINARY_DIR}/org-bluez-device1.c
                   COMMAND gdbus-codegen --interface-prefix org.bluez.Device1. --generate-c-code ${CMAKE_CURRENT_BINARY_DIR}/org-bluez-device1 ${CMAKE_CURRENT_SOURCE_DIR}/org.bluez.Device1.xml
                   DEPENDS ${CMAKE_CURRENT_SOURCE_DIR}/org.bluez.Device1.xml
                   COMMENT "Generate D-Bus 'org.bluez.Device1.xml'"
                   )

add_custom_command(OUTPUT ${CMAKE_CURRENT_BINARY_DIR}/org-bluez-gattcharacteristic1.c
                   COMMAND gdbus-codegen --interface-prefix org.bluez.GattCharacteristic1. --generate-c-code ${CMAKE_CURRENT_BINARY_DIR}/org-bluez-gattcharacteristic1 ${CMAKE_CURRENT_SOURCE_DIR}/org.bluez.GattCharacteristic1.xml
                   DEPENDS ${CMAKE_CURRENT_SOURCE_DIR}/org.bluez.GattCharacteristic1.xml
                   COMMENT "Generate D-Bus 'org.bluez.GattCharacteristic1.xml'"
                   )

add_custom_command(OUTPUT ${CMAKE_CURRENT_BINARY_DIR}/org-bluez-gattservice1.c
                   COMMAND gdbus-codegen --interface-prefix org.bluez.GattService1. --generate-c-code ${CMAKE_CURRENT_BINARY_DIR}/org-bluez-gattservice1 ${CMAKE_CURRENT_SOURCE_DIR}/org.bluez.GattService1.xml
                   DEPENDS ${CMAKE_CURRENT_SOURCE_DIR}/org.bluez.GattService1.xml
                   COMMENT "Generate D-Bus 'org.bluez.GattService1.xml'"
                   )

add_library(
		BluezDbusLib
		uuid.c
		org-bluez-adapter1.c
		org-bluez-battery1.c
		org-bluez-device1.c
		org-bluez-gattcharacteristic1.c
		org-bluez-gattservice1.c
		)

target_link_libraries(
		BluezDbusLib
		PkgConfig::GLIB
		PkgConfig::GIO_UNIX
		)
