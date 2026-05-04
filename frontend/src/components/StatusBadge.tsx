export default function StatusBadge({ estado }: { estado: string }) {
  const base = "inline-flex items-center rounded-full px-3 py-1 text-sm font-semibold";
  const styles =
    estado === "APROBADO"
      ? "bg-green-100 text-green-800"
      : "bg-red-100 text-red-800";
  return <span className={`${base} ${styles}`}>{estado}</span>;
}
