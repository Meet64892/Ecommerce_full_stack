import { useQuery } from '@tanstack/react-query';
import { adminApi } from '@api/adminApi';
import { ROLE_LABELS } from '@utils/roles';

export default function AdminUsersPage() {
  const { data, isLoading } = useQuery({
    queryKey: ['admin', 'users'],
    queryFn: () => adminApi.listUsers(0, 50),
  });

  if (isLoading) return <p className="text-slate-400">Loading users…</p>;

  const users = data?.content ?? [];

  return (
    <div>
      <h1 className="mb-4 font-display text-xl font-bold text-slate-100">User management</h1>
      <div className="overflow-x-auto rounded-xl border border-slate-700/50">
        <table className="min-w-full text-left text-sm">
          <thead className="bg-slate-800/80 text-slate-300">
            <tr>
              <th className="px-4 py-3">Name</th>
              <th className="px-4 py-3">Email</th>
              <th className="px-4 py-3">Role</th>
            </tr>
          </thead>
          <tbody>
            {users.map((u) => (
              <tr key={u.id} className="border-t border-slate-700/40">
                <td className="px-4 py-3 text-slate-100">{u.fullName}</td>
                <td className="px-4 py-3 text-slate-400">{u.email}</td>
                <td className="px-4 py-3 text-slate-300">{ROLE_LABELS[u.role]}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
