import csv
from pathlib import Path

import matplotlib

matplotlib.use("Agg")
import matplotlib.pyplot as plt


root = Path(__file__).resolve().parents[1]
with (root / "results/tables/benchmark.csv").open(newline="", encoding="utf-8") as file:
    rows = list(csv.DictReader(file))

output = root / "results/plots"
output.mkdir(parents=True, exist_ok=True)


def values(algorithm, workload, variant):
    selected = [row for row in rows if row["algorithm"] == algorithm
                and row["workload"] == workload and row["variant"] == variant]
    selected.sort(key=lambda row: int(row["input_size"]))
    return [int(row["input_size"]) for row in selected], [int(row["avg_nanoseconds"]) / 1_000_000 for row in selected]


fig, ax = plt.subplots(figsize=(8, 5))
for algorithm, label in [("LCS", "LCS"), ("EditDistance", "Edit Distance")]:
    x, y = values(algorithm, "dynamic_programming", "random")
    ax.plot(x, y, marker="o", linewidth=2, label=label)
ax.set(title="Dynamic programming time", xlabel="Length of each string", ylabel="Average time (ms)")
ax.grid(alpha=0.3)
ax.legend()
fig.tight_layout()
fig.savefig(output / "dp_times.png", dpi=160)
plt.close(fig)

fig, axes = plt.subplots(1, 2, figsize=(12, 5), sharey=True)
for ax, variant in zip(axes, ["present", "absent"]):
    for algorithm, label in [("Naive", "Naive"), ("KMP", "KMP"), ("RabinKarp", "Rabin-Karp")]:
        x, y = values(algorithm, "matching", variant)
        ax.plot(x, y, marker="o", linewidth=2, label=label)
    ax.set(title=f"Pattern {variant}", xlabel="Text length")
    ax.grid(alpha=0.3)
    ax.legend()
axes[0].set_ylabel("Average time (ms)")
fig.suptitle("String matching time")
fig.tight_layout()
fig.savefig(output / "matching_times.png", dpi=160)
plt.close(fig)

fig, ax = plt.subplots(figsize=(8, 5))
x, y = values("Kasai", "lcp", "random")
ax.plot(x, y, marker="o", linewidth=2, color="tab:green")
ax.set(title="Kasai LCP time", xlabel="String length", ylabel="Average time (ms)")
ax.grid(alpha=0.3)
fig.tight_layout()
fig.savefig(output / "kasai_times.png", dpi=160)
plt.close(fig)

print("Saved three plots in results/plots")
