# Overview

A simple properties resolver service inspired by [Nim](https://nim-lang.org)'s [parseutil](https://nim-lang.org/docs/parseutils.html) module.

## Usage

```java
Map<String, String> properties = new HashMap<String, String>();
properties.put("key1", "world");
properties.put("key2", "hello");
properties.put("key3", "${key2} ${key1}");

Map<String, String> resolved = PropertiesResolverService.resolve(properties);
resolved.entrySet()
        .forEach(it -> System.out.println(it.getKey() + "=" + it.getValue()));
```

It will print something like this:

```bash
key1=world
key2=hello
key3=hello world
```
