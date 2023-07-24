import java.util.stream.Collectors

print([["1", "2"], ["3", "4"]].stream().flatMap { it.stream()}.collect(Collectors.toList()))

print([["1", "2"], ["3", "4"]].collectMany { it })