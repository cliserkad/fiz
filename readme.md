# fiz

A JVM language built for simplicity and usability.  
[fiz.dev](https://fiz.dev)  
[repo](https://github.com/cliserkad/fiz)  

# Installation

## [Template Repository](https://github.com/cliserkad/fiz-base)

Follow the link and jump right in!

## Maven Plugin

Downloading the plugin from GitHub packages requires you to make a `settings.xml` file with your GitHub credentials.

## Build From Source

Clone this repo and install with Maven.

```shell script
git clone https://github.com/cliserkad/fiz
cd fiz
mvn clean install
```

# Hello World

```fiz
main {
	println("Hello, World")
}
```

# Planned Features

- Default Arguments
- Named Parameters
- Immutable by default
- Non-Null by default
- Build system
