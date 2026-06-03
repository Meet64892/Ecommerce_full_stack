import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { adminApi } from '@api/adminApi';
import { ROLE_LABELS } from '@utils/roles';
import type { UserRole } from '@/types/product.types';
import toast from 'react-hot-toast';
import { parseApiError } from '@utils/errorHandler';

const ASSIGNABLE_ROLES: UserRole[] = ['USER', 'SUPER_USER', 'SUPER_ADMIN'];

export default function AdminUsersPage() {
  const queryClient = useQueryClient();
  const { data, isLoading } = useQuery({
    queryKey: ['admin', 'users'],
    queryFn: () => adminApi.listUsers(0, 50),
  });

  const changeRole = useMutation({
    mutationFn: ({ userId, role }: { userId: string; role: UserRole }) => adminApi.updateUserRole(userId, role),
    onSuccess: (user) => {
      toast.success(`Role updated to ${ROLE_LABELS[user.role]}`);
      void queryClient.invalidateQueries({ queryKey: ['admin', 'users'] });
    },
    onError: (e) => toast.error(parseApiError(e)),
  });

  if (isLoading) return <p className="text-slate-400">Loading users…</p>;

  const users = data?.content ?? [];

  return (
    <div>
      <h1 className="mb-2 font-display text-xl font-bold text-slate-100">User management</h1>
      <p className="mb-4 text-sm text-slate-400">
        Promote a customer to vendor admin (Admin / brand owner) or adjust platform roles.
      </p>
      <div className="overflow-x-auto rounded-xl border border-slate-700/50">
        <table className="min-w-full text-left text-sm">
          <thead className="bg-slate-800/80 text-slate-300">
            <tr>
              <th className="px-4 py-3">Name</th>
              <th className="px-4 py-3">Email</th>
              <th className="px-4 py-3">Role</th>
              <th className="px-4 py-3">Change role</th>
            </tr>
          </thead>
          <tbody>
            {users.map((u) => (
              <tr key={u.id} className="border-t border-slate-700/40">
                <td className="px-4 py-3 text-slate-100">{u.fullName}</td>
                <td className="px-4 py-3 text-slate-400">{u.email}</td>
                <td className="px-4 py-3 text-slate-300">{ROLE_LABELS[u.role]}</td>
                <td className="px-4 py-3">
                  <select
                    className="rounded-lg border border-slate-600 bg-slate-800 px-2 py-1.5 text-slate-100"
                    value={u.role}
                    disabled={changeRole.isPending}
                    onChange={(e) => {
                      const role = e.target.value as UserRole;
                      if (role !== u.role) {
                        changeRole.mutate({ userId: u.id, role });
                      }
                    }}
                  >
                    {ASSIGNABLE_ROLES.map((r) => (
                      <option key={r} value={r}>
                        {ROLE_LABELS[r]}
                      </option>
                    ))}
                  </select>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
