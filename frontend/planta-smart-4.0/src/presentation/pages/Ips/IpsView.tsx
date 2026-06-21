import { useIpsViewModel } from '@pages/Ips/useIpsViewModel';
import { AppTemplate } from '@components/template/AppTemplate';
import { FeedbackBanner } from '@components/atoms/FeedbackBanner/FeedbackBanner';
import styles from './ipsView.module.css';
import image from '@assets/bancada/Smart40.png';


const MODULE_CLASSES: Record<string, string> = {
  estoque:   'estoque',
  processo:  'processo',
  montagem:  'montagem',
  expedicao: 'expedicao',
};

export default function IpsView() {
  const {
    model,
    handleFaixaChange,
    confirmarFaixa,
    conectar,
    toggleReadOnly,
    dismissErro,
    dismissSucesso,
  } = useIpsViewModel();

  return (
    <AppTemplate>
      <main id="main-content" className={styles.main}>

        {/* ── Painel esquerdo ── */}
        <section className={styles.enderecos}>
          {model.erro && (
            <FeedbackBanner
              variant="error"
              message={model.erro}
              onDismiss={dismissErro}
            />
          )}
          {model.sucesso && (
            <FeedbackBanner
              variant="success"
              message={model.sucesso}
              onDismiss={dismissSucesso}
            />
          )}

          <div>
            <p className={styles.sectionLabel}>Endereços IP</p>
            <div className={styles.ips}>
              {model.modulos.map((modulo) => {
                const mod = MODULE_CLASSES[modulo.key];
                return (
                  <div
                    key={modulo.key}
                    className={`${styles.ipCard} ${styles[mod]}`}
                  >
                    <span className={styles.ipLabel}>
                      <span className={`${styles.ipDot} ${styles[mod]}`} />
                      {modulo.label}
                    </span>
                    <span className={styles.ipValue}>{modulo.ip}</span>
                  </div>
                );
              })}
            </div>
          </div>

          <hr className={styles.divider} />

          <div className={styles.fieldGroup}>
            <span className={styles.fieldLabel}>Faixa de rede</span>
            <div className={styles.fieldRow}>
              <input
                id="faixa"
                type="text"
                className={styles.faixaInput}
                value={model.faixa}
                placeholder="Ex: 10.74.241.0"
                onChange={(e) => handleFaixaChange(e.target.value)}
              />
              <button
                type="button"
                className={styles.btnSecondary}
                onClick={confirmarFaixa}
              >
                Confirmar
              </button>
            </div>
          </div>

          <label className={styles.readOnlyRow}>
            <input
              type="checkbox"
              className={styles.readOnlyCheckbox}
              checked={model.readOnly}
              onChange={(e) => toggleReadOnly(e.target.checked)}
            />
            <span className={styles.readOnlyLabel}>
              Modo readOnly
            </span>
          </label>

          <button
            type="button"
            className={styles.btnConectar}
            disabled={model.loading}
            onClick={conectar}
          >
            {model.loading ? 'Conectando…' : 'Conectar'}
          </button>
        </section>

        {/* ── Painel direito ── */}
        <aside className={styles.bancada}>
          <img
            src={image}
            alt="Bancada Smart 4.0"
            className={styles.bancadaImg}
          />
        </aside>

      </main>
    </AppTemplate>
  );
}