package dev.emi.emi.search;

import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import dev.emi.emi.EmiPort;
import dev.emi.emi.EmiUtil;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Identifier;

public class RegexTagQuery extends Query {
	private final Set<Object> valid;

	public RegexTagQuery(String name) {
		Pattern p = null;
		try {
			p = Pattern.compile(name, Pattern.CASE_INSENSITIVE);
		} catch (Exception e) {
		}
		if (p == null) {
			valid = Set.of();
		} else {
			final Pattern pat = p;
			valid = Stream.concat(
				EmiPort.getItemRegistry().streamTags().filter(t -> {
					Identifier id = t.id();
					String translation = EmiUtil.translateId("tag.", id);
					if (I18n.hasTranslation(translation)) {
						if (pat.matcher(EmiPort.translatable(translation).getString().toLowerCase()).find()) {
							return true;
						}
					}
					if (pat.matcher(id.toString()).find()) {
						return true;
					}
					return false;
				}).map(t -> EmiPort.getItemRegistry().getEntryList(t)), EmiPort.getBlockRegistry().streamTags().filter(t -> {
					if (pat.matcher(t.id().toString()).find()) {
						return true;
					}
					return false;
				}).map(t -> EmiPort.getBlockRegistry().getEntryList(t))).flatMap(v -> v.stream()).collect(Collectors.toSet());
		}
	}

	@Override
	public boolean matches(EmiStack stack) {
		return valid.contains(stack.getKey());
	}
}
